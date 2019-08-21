import { HomePage } from '../../page-objects/dossiers-page.po';
import { LoginPage } from '../../page-objects/login-page.po';
import { browser } from "protractor";

describe(`Feature: Search and Delete a Dossiers`, async () => {
    let homePage: HomePage;
    let loginPage: LoginPage;
    const dossierToSearchFor = `Toy Story 3`;

    beforeAll(async () => {
        homePage = new HomePage();
        loginPage = new LoginPage();
        await loginPage.waitForApplication();
    });

    afterEach( async () => {
        await browser.refresh();
    });

    // TODO -- Delete a dossier
    // This test currently only checks the presence of the delete button
    it(`I search for a dossier with the following name: ${dossierToSearchFor} and confirm the delete button is present`, async () => {
        await loginPage.login(`business-supervisor`, `password`);
        await homePage.searchForDossier(dossierToSearchFor);
        const searchResults = await homePage.waitForPresenceAndGetDossierMainSection();
        const searchResultsText = await searchResults.getText();
        expect(searchResultsText).toContain(dossierToSearchFor);
        const deleteButton = await homePage.waitForPresenceAndGetDeleteButton();

        const deleteButtonPresence = await deleteButton.isPresent();

        expect(deleteButtonPresence).toBe(true);
    });

    it(`I search for a dossier with the following name: ${dossierToSearchFor} and confirm the delete button is not present`, async () => {
        await loginPage.login(`business-user`, `password`);
        await homePage.searchForDossier(dossierToSearchFor);
        const searchResults = await homePage.waitForPresenceAndGetDossierMainSection();
        const searchResultsText = await searchResults.getText();
        expect(searchResultsText).toContain(dossierToSearchFor);
        const deleteButton = await homePage.getDeleteButton();

        const deleteButtonPresence = await deleteButton.isPresent();

        expect(deleteButtonPresence).toBe(false);
    });
});
