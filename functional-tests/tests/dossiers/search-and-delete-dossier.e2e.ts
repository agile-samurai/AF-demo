import { HomePage } from '../../page-objects/dossiers-page.po';

describe(`Feature: Search and Delete a Dossiers`, async () => {
    let homePage: HomePage;
    const dossierToSearchFor = `Toy Story 3`;

    beforeAll(async () => {
        homePage = new HomePage();
        await homePage.waitForApplication();
    });

    // TODO -- Delete a dossier
    // This test currently only checks the presence of the delete button
    it(`I search and delete a dossier with the following name: ${dossierToSearchFor}`, async () => {
        beforeAll( async () => {
            await homePage.searchForDossier(dossierToSearchFor);
            const searchResults = await homePage.waitForPresenceAndGetDossierMainSection();
            const searchResultsText = await searchResults.getText();

            expect(searchResultsText).toContain(dossierToSearchFor);
        });

        const deleteButton = await homePage.waitForPresenceAndGetDeleteButton();

        const deleteButtonPresence = await deleteButton.isPresent();

        expect(deleteButtonPresence).toBe(true);
    });
});
