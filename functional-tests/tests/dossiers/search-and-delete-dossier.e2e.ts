import { HomePage } from '../../page-objects/dossiers-page.po';

xdescribe(`Feature: Search and Delete a Dossiers`, async () => {
    let homePage: HomePage;
    const dossierToSearchFor = `The Other Side of the Wind`;

    beforeAll(async () => {
        homePage = new HomePage();
        await homePage.waitForApplication();
    });

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
