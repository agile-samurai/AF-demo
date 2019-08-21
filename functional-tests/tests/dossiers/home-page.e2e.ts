import { HomePage } from '../../page-objects/dossiers-page.po';
import { LoginPage } from '../../page-objects/login-page.po';
import { browser } from 'protractor';

describe(`Feature: Land on the home page`, async () => {
    let homePage: HomePage;
    let loginPage: LoginPage;

    beforeAll(async () => {
        homePage = new HomePage();
        loginPage = new LoginPage();

        await homePage.waitForApplication();
        await loginPage.login(`business-user`, `password`);
    });

    afterAll( async () => {
        await browser.refresh();
    });

    it(`I confirm the logo is present`, async () => {
        const logo = await homePage.waitForPresenceAndGetLogo();

        const logoPresence = await logo.isPresent();

        expect(logoPresence).toBe(true);
    });

    it(`I confirm the search bar is present and has the expected placeholder`, async () => {
        const searchBar = await homePage.waitForPresenceAndGetSearchBar();

        const placeholderText = await searchBar.getAttribute(`placeholder`);

        expect(placeholderText).toBe(`Search for dossier`);
    });

    it(`I confirm there are search results`, async () => {
        const searchResults = await homePage.waitForPresenceAndGetSearchResults();

        const searchResultsPresence = await searchResults.isPresent();

        expect(searchResultsPresence).toBe(true);
    });
});
