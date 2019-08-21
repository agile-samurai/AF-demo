import { browser, ElementFinder } from 'protractor';
import { CommonActions } from '../common/actions';

export class LoginPage extends CommonActions  {
    private url: string = browser.params.baseUrl;
    private username: string = `form input[type=text]`;
    private password: string = `form input[type=password]`;
    private submitButton: string = `form input[type=submit]`;

    public async waitForApplication(): Promise<void> {
        await browser.get(this.url);
    }

    public async waitForPresenceAndGetUserName(): Promise<ElementFinder> {
        return this.waitForPresenceAndGetElementByCss(this.username);
    }

    public async waitForPresenceAndGetPassword(): Promise<ElementFinder> {
        return this.waitForPresenceAndGetElementByCss(this.password);
    }

    public async waitForPresenceAndGetSubmitButton(): Promise<ElementFinder> {
        return this.waitForPresenceAndGetElementByCss(this.submitButton);
    }

    public async login(username: string, password: string): Promise<void> {
        const userNameField = await this.waitForPresenceAndGetUserName();
        const passwordField = await this.waitForPresenceAndGetPassword();
        const submitButton = await this.waitForPresenceAndGetSubmitButton();

        await userNameField.sendKeys(username);
        await passwordField.sendKeys(password);
        await this.waitForClickable(submitButton);
        await submitButton.click();
    }
}
