import { ElementFinder, browser } from 'protractor';
import { CommonActions } from '../common/actions';

export class CelebrityPage extends CommonActions  {
    private url: string = `${browser.params.baseUrl}celebrity-search`;
    private logo: string = `.navigation-link`;
    private searchBar: string = `#standard-name`;

    public async waitForApplication(): Promise<void> {
        await this.goToAndWaitForPage(this.url, /celebrity-search/);
    }

    public async waitForPresenceAndGetLogo(): Promise<ElementFinder> {
        return this.waitForPresenceAndGetElementByCss(this.logo);
    }

    public async waitForPresenceAndGetSearchBar(): Promise<ElementFinder> {
        return this.waitForPresenceAndGetElementByCss(this.searchBar);
    }

    public async searchForCelebtirty(dossier: string): Promise<ElementFinder> {
        const searchBar = await this.waitForPresenceAndGetSearchBar();
        await searchBar.clear;
        await searchBar.sendKeys(dossier);
    }
}
