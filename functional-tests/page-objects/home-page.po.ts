import { ElementFinder } from 'protractor';
import { CommonActions } from '../common/actions';

export class HomePage extends CommonActions  {
    private url: string = `http://localhost:9091/`;
    private body: string = `body [id=root]`;

    public async waitForApplication(): Promise<void> {
        await this.goToAndWaitForPage(this.url, /9091/);
    }

    public async waitForPresenceAndGetBody(): Promise<ElementFinder> {
        return this.waitForPresenceAndGetElementByCss(this.body);
    }
}
