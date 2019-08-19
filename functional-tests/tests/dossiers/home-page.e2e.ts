import { HomePage } from '../../page-objects/dossiers-page.po';

describe(`Feature: Land on the home page`, async () => {
    let homePage: HomePage;

    beforeAll(async () => {
        homePage = new HomePage();
        await homePage.waitForApplication();
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
