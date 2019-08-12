import { browser, ElementFinder, ExpectedConditions, element, by } from 'protractor';

export class CommonActions  {

    private timeOut: number = 10000;

    public byCss(selector: string): ElementFinder {
        return element(by.css(selector));
    }

    public byId(id: string): ElementFinder {
        return element(by.id(id));
    }

    public byTagName(selector: string): ElementFinder {
        return element(by.tagName(selector));
    }

    public byCssContainingText(selector: string, text: string): ElementFinder {
        return element(by.cssContainingText(selector, text));
    }

    public async waitForPresence(locator: string | ElementFinder, timeout: number = this.timeOut): Promise<void> {
        if (typeof locator === "string") {
            locator = this.byCss(locator);
        }
        await browser.wait(ExpectedConditions.presenceOf(locator), timeout, `The Element is most likely not present`);
    }

    public async waitForAbsence(locator: string | ElementFinder, timeout: number = this.timeOut): Promise<void>  {
        if (typeof locator === "string") {
            locator = this.byCss(locator);
        }
        await browser.wait(ExpectedConditions.not(ExpectedConditions.presenceOf(locator)), timeout, 'The Element is most likely still present');
    }

    public async waitForClickable(locator: string | ElementFinder, timeout: number = this.timeOut): Promise<void>  {
        if (typeof locator === "string") {
            locator = this.byCss(locator);
        }
        await browser.wait(ExpectedConditions.elementToBeClickable(locator), timeout, 'The Element is most likely not clickable');
    }

    public async waitForNotClickable(locator: string | ElementFinder, timeout: number = this.timeOut): Promise<void>  {
        if (typeof locator === "string") {
            locator = this.byCss(locator);
        }
        await browser.wait(ExpectedConditions.not(ExpectedConditions.elementToBeClickable(locator)), timeout, 'The Element is most likely still clickable');
    }

    public async waitForVisible(locator: string | ElementFinder, timeout: number = this.timeOut): Promise<void>  {
        if (typeof locator === "string") {
            locator = this.byCss(locator);
        }
        await browser.wait(ExpectedConditions.visibilityOf(locator), timeout, 'The Element is most likely not visible');
    }

    public async waitForNotVisible(locator: string | ElementFinder, timeout: number = this.timeOut): Promise<void>  {
        if (typeof locator === "string") {
            locator = this.byCss(locator);
        }
        await browser.wait(ExpectedConditions.invisibilityOf(locator), timeout, 'The Element is most likely still visible');
    }

    public async waitForPresenceAndGetElementByCss(selector): Promise<ElementFinder> {
        const targetElement: ElementFinder = this.byCss(selector);
        await this.waitForPresence(targetElement);
        return this.byCss(selector);
    }

    public async waitForPresenceAndGetElementByCssContainingText(selector: string, text: string): Promise<ElementFinder> {
        const targetElement: ElementFinder = this.byCss(selector);
        await this.waitForPresence(targetElement);
        return this.byCssContainingText(selector, text);
    }

    public async goToAndWaitForPage(targetUrl: string, expectedExtension: RegExp): Promise<boolean> {
        return browser.get(targetUrl).then(() => {
            return browser.wait(() => {
                return browser.driver.getCurrentUrl().then((currentUrl: string) => {
                    return expectedExtension.test(currentUrl);
                });
            }, this.timeOut, 'Expected extension not find in URL');
        });
    }
}
