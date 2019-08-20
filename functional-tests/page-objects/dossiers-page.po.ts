import { ElementFinder } from 'protractor';
import { CommonActions } from '../common/actions';

export class HomePage extends CommonActions  {
    private url: string = `http://localhost:9091/`;
    private logo: string = `.navigation-link`;
    private searchBar: string = `#standard-name`;
    private searchResults: string = `.search-results`;
    private dossierMainSection: string = `${this.searchResults} .dossier-main-section`;
    private deleteButton: string = `${this.dossierMainSection} button[class*=delete-dossier-button]`;
    private addNoteButton: string = `${this.dossierMainSection} button[class*=add-note-button]`;

    public async waitForApplication(): Promise<void> {
        await this.goToAndWaitForPage(this.url, /9091/);
    }

    public async waitForPresenceAndGetLogo(): Promise<ElementFinder> {
        return this.waitForPresenceAndGetElementByCss(this.logo);
    }

    public async waitForPresenceAndGetSearchBar(): Promise<ElementFinder> {
        return this.waitForPresenceAndGetElementByCss(this.searchBar);
    }

    public async waitForPresenceAndGetSearchResults(): Promise<ElementFinder> {
        return this.waitForPresenceAndGetElementByCss(this.searchResults);
    }

    public async waitForPresenceAndGetDossierMainSection(): Promise<ElementFinder> {
        return this.waitForPresenceAndGetElementByCss(this.dossierMainSection);
    }

    public async waitForPresenceAndGetDeleteButton(): Promise<ElementFinder> {
        return this.waitForPresenceAndGetElementByCss(this.deleteButton);
    }

    public async waitForPresenceAndGetAddNoteButton(): Promise<ElementFinder> {
        return this.waitForPresenceAndGetElementByCss(this.addNoteButton);
    }

    public async searchForDossier(dossier: string): Promise<ElementFinder> {
        const searchBar = await this.waitForPresenceAndGetSearchBar();
        await searchBar.clear;
        await searchBar.sendKeys(dossier);
    }
}