import { HomePage } from '../page-objects/dossiers-page.po';
import { LoginPage } from '../page-objects/login-page.po';
import { browser } from 'protractor';

describe(`Feature: Login`, async () => {
    let homePage: HomePage;
    let loginPage: LoginPage;

    beforeAll(async () => {
        homePage = new HomePage();
        loginPage = new LoginPage();

        await loginPage.waitForApplication();
    });

    afterEach( async () => {
        await browser.refresh()
    });

    it(`I successfully login with valid credentials as a business user`, async () => {
        const testUser = `business-user`;
        const testPassword = `password`;
        await loginPage.login(testUser,testPassword);

        const searchResults = await homePage.waitForPresenceAndGetSearchResults();
        const logo = await homePage.waitForPresenceAndGetLogo();
        const searchResultsPresence = await searchResults.isPresent();
        const logoPresence = await logo.isPresent();
        const searchBar = await homePage.waitForPresenceAndGetSearchBar();
        const placeholderText = await searchBar.getAttribute(`placeholder`);

        expect(placeholderText).toBe(`Search for dossier`);
        expect(logoPresence).toBe(true);
        expect(searchResultsPresence).toBe(true);
    });

    it(`I successfully login with valid credentials as a business supervisor`, async () => {
        const testUser = `business-supervisor`;
        const testPassword = `password`;
        await loginPage.login(testUser,testPassword);

        const searchResults = await homePage.waitForPresenceAndGetSearchResults();
        const logo = await homePage.waitForPresenceAndGetLogo();
        const searchResultsPresence = await searchResults.isPresent();
        const logoPresence = await logo.isPresent();
        const searchBar = await homePage.waitForPresenceAndGetSearchBar();
        const placeholderText = await searchBar.getAttribute(`placeholder`);

        expect(placeholderText).toBe(`Search for dossier`);
        expect(logoPresence).toBe(true);
        expect(searchResultsPresence).toBe(true);
    });
});
