import { Semaphore, type Permit } from "@shopify/semaphore";
import { InsertOperation, type Operation } from "./operations";

export default class OperationQueue {
  private operations: Operation[] = [];
  private mutex = new Semaphore(1);

  public async push(operation: Operation): Promise<boolean> {
    const permit = await this.mutex.acquire();

    for(let i = 0; i < this.operations.length; i++) {
      const currentOperation = this.operations[i];
      if(currentOperation instanceof InsertOperation && operation instanceof InsertOperation) {
        const insertOperation = currentOperation as InsertOperation;
        if(insertOperation.index + insertOperation.length === operation.index) {
          insertOperation.content += operation.content;
          insertOperation.length += operation.length;
          this.operations[i] = insertOperation;
          permit.release();
          return false;
        }
      }
    }

    this.operations.push(operation);
    permit.release();
    return true;
  }

  public async shift() {
    const permit = await this.mutex.acquire();
    const operation = this.operations.shift();
    permit.release();
    return operation;
  }

  public async map(callback: (operation: Operation) => Operation | null) {
    const permit = await this.mutex.acquire();
    this.operations = this.operations.map(callback).filter((op): op is Operation => op !== null);
    permit.release();
  }

  public forEach(callback: (operation: Operation) => void) {
    this.operations.forEach(callback);
  }

}