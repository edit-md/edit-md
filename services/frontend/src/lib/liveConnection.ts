import { Permit, Semaphore } from "@shopify/semaphore";
import type { Operation } from "./operations";
import type OperationQueue from "./operationQueue";

type ReceivedMessage = {
    commandId: number,
    data: string,
    original: MessageEvent,
}

export default class LiveConnection {
    private endpoint: string;
    private socket!: WebSocket;
    private closeRequested = false;
    private waitSemaphore = new Semaphore(1);
    private waitingForAck: {
        obj: any,
        permit: Permit,
    }[] = []

    private onMessageCallbacks: ((data: ReceivedMessage) => void)[] = [];
    private onOpenCallbacks: (() => void)[] = [];

    constructor(endpoint: string) {
        this.endpoint = endpoint;
        this.setupSocket();

        setInterval(() => {
            this.send(this.convertToCommand(255, ''));
        }, 30000);
    }

    private setupSocket() {
        this.socket = new WebSocket(this.endpoint);
        this.socket.onclose = () => {
            if(this.closeRequested) 
                return;

            setTimeout(() => {
                this.setupSocket();
            }, 1000);
        }

        this.socket.onmessage = async (event) => {
            const arrayBuffer = await event.data.arrayBuffer();
            const dataView = new DataView(arrayBuffer);

            // Extract the first byte as commandId
            const commandId = dataView.getUint8(0);

            // Extract the rest of the data starting from byte 1
            const restData = arrayBuffer.slice(1);

            // Decode the rest of the data into a string
            const textDecoder = new TextDecoder("utf-8");
            const data = textDecoder.decode(restData);
            
            this.onMessageCallbacks.forEach(callback => callback({
                commandId: commandId,
                data: data,
                original: event,
            }));
        }

        this.socket.onopen = () => {
            console.log(`Connected to live connection at ${this.endpoint}`);
            this.onOpenCallbacks.forEach(callback => callback());
        }
    }

    getWaitingForAck() {
        return this.waitingForAck;
    }

    ack() {
        this.waitingForAck.shift()?.permit.release();
    }

    onMessage(callback: (data: ReceivedMessage) => void) {
        this.onMessageCallbacks.push(callback);
    }

    onOpen(callback: () => void) {
        this.onOpenCallbacks.push(callback);
    }

    send(data: any) {
        if(this.socket.readyState !== WebSocket.OPEN) {
            return;
        }
        
        this.socket.send(data);
    }

    async queue(data: any) {
        let permit = await this.waitSemaphore.acquire();
        this.waitingForAck.push({ obj: data, permit });
        this.send(data);
    }

    async queueFromQueue(queue: OperationQueue) {
        let permit = await this.waitSemaphore.acquire();
        let op = await queue.shift();

        if(op) {
            this.waitingForAck.push({ obj: op, permit });
            let data = this.convertToCommand(op.commandId, op.toTransport());
            this.send(data);
        } else {
            permit.release();
        }
    }

    convertToCommand(commandId: number, data: string | object) {
		// Encode the string to UTF-8
		const encoder = new TextEncoder(); // Built-in UTF-8 encoder

		if(typeof data === 'object') {
			data = JSON.stringify(data);
		}

		const utf8Bytes = encoder.encode(data);

		// Create an ArrayBuffer to hold the first byte + string bytes
		const messageBuffer = new Uint8Array(1 + utf8Bytes.length);

		// Set the first byte to the command ID
		messageBuffer[0] = commandId;

		// Append the UTF-8 encoded string
		messageBuffer.set(utf8Bytes, 1);

        return messageBuffer;
	}

    close() {
        this.closeRequested = true;
        this.socket.close();
    }
}