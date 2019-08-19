import { HomePage } from '../../page-objects/dossiers-page.po';

describe(`Feature: Search and View a Dossiers`, async () => {
    let homePage: HomePage;
    const dossierToSearchFor = `The Other Side of the Wind`;

    beforeAll(async () => {
        homePage = new HomePage();
        await homePage.waitForApplication();
    });

    it(`I search for a dossier with the following name: ${dossierToSearchFor}`, async () => {
        await homePage.searchForDossier(dossierToSearchFor);
        const searchResults = await homePage.waitForPresenceAndGetDossierMainSection();
        const searchResultsText = await searchResults.getText();

        expect(searchResultsText).toContain(dossierToSearchFor);
        expect(searchResultsText).toContain(`A Hollywood director emerges from semi-exile with plans to complete work on an innovative motion picture.`);
        expect(searchResultsText).toContain(`The Other Side of the Wind is a movie starring John Huston, Oja Kodar, and Peter Bogdanovich. A Hollywood director emerges from semi-exile with plans to complete work on an innovative motion picture.`);
        expect(searchResultsText).toContain(`Jake Hannaford`);
        expect(searchResultsText).toContain(`Lou Martin`);
    });
});
