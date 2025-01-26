import { Semaphore, type Permit } from "@shopify/semaphore";
import type { Operation } from "./operations";

export default class OperationQueue {
  private operations: Operation[] = [];
  private mutex = new Semaphore(1);

  public async push(operation: Operation) {
    const permit = await this.mutex.acquire();
    this.operations.push(operation);
    permit.release();
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