export abstract class Operation {
    revision: number;
    index = 0;
    abstract commandId: number;
    abstract length: number;

    constructor(revision: number, index: number) {
        this.revision = revision;
        this.index = index;
    }

    abstract toTransport(): object;
}

export class InsertOperation extends Operation {
    commandId = 0;
    content = '';
    length = this.content.length;

    constructor(revision: number, index: number, content: string) {
        super(revision, index);
        this.content = content;
        this.length = content.length;
    }

    static fromOperation(operation: Operation, override?: { index?: number, content?: string }) {
        return new InsertOperation(operation.revision, override?.index ?? operation.index, override?.content ?? '');
    }

    toTransport(): object {
        return {
            r: this.revision,
            i: this.index,
            c: this.content,
        };
    }
}

export class DeleteOperation extends Operation {
    commandId = 1;
    length = 0;

    constructor(revision: number, index: number, length: number) {
        super(revision, index);
        this.length = length;
    }

    static fromOperation(operation: Operation, override?: { index?: number, length?: number }) {
        return new DeleteOperation(operation.revision, override?.index ?? operation.index, override?.length ?? operation.length);
    }

    toTransport(): object {
        return {
            r: this.revision,
            i: this.index,
            l: this.length,
        };
    }
}

/**
 * Transforms op1 against op2, returning the transformed operation.
 * Returns null if the transformation results in no operation.
 * @param op1 - The first operation to transform.
 * @param op2 - The operation to transform against.
 * @returns The transformed operation or null.
 */
export function transform(op1: Operation, op2: Operation): Operation | null {
    if (op1 instanceof InsertOperation && op2 instanceof InsertOperation) {
        if (op1.index < op2.index) {
            return new InsertOperation(op1.revision, op1.index, op1.content);
        }

        return new InsertOperation(op1.revision, op1.index + op2.content.length, op1.content);
    } else if (op1 instanceof DeleteOperation && op2 instanceof DeleteOperation) {
        const op1End = op1.index + op1.length;
        const op2End = op2.index + op2.length;

        if (op1End <= op2.index) {
            // op1 occurs entirely before op2
            return new DeleteOperation(op1.revision, op1.index, op1.length);
        } else if (op2End <= op1.index) {
            // op2 occurs entirely before op1
            return new DeleteOperation(op1.revision, op1.index - op2.length, op1.length);
        } else {
            // Overlapping delete operations
            const start = Math.min(op1.index, op2.index);
            const end = Math.max(op1End, op2End);
            return new DeleteOperation(op1.revision, start, end - start);
        }
    } else if (op1 instanceof InsertOperation && op2 instanceof DeleteOperation) {
        const deleteEnd = op2.index + op2.length;

        if (op1.index < op2.index) {
            return new InsertOperation(op1.revision, op1.index, op1.content);
        } else if (op1.index >= op2.index && op1.index < deleteEnd) {
            // Insert occurs within the delete range, discard the insert
            return null;
        } else {
            return new InsertOperation(op1.revision, op1.index - op2.length, op1.content);
        }
    } else if (op1 instanceof DeleteOperation && op2 instanceof InsertOperation) {
        if (op1.index >= op2.index) {
            return new DeleteOperation(op1.revision, op1.index + op2.length, op1.length);
        }
        return new DeleteOperation(op1.revision, op1.index, op1.length);
    }

    return null;
}