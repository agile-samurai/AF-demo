import { HomePage } from '../../page-objects/dossiers-page.po';

describe(`Feature: Search and View a Dossiers`, async () => {
    let homePage: HomePage;
    const dossierToSearchFor = `Toy Story 3`;

    beforeAll(async () => {
        homePage = new HomePage();
        await homePage.waitForApplication();
    });

    it(`I search for a dossier with the following name: ${dossierToSearchFor}`, async () => {
        await homePage.searchForDossier(dossierToSearchFor);
        const searchResults = await homePage.waitForPresenceAndGetDossierMainSection();
        const searchResultsText = await searchResults.getText();

        expect(searchResultsText).toContain(dossierToSearchFor);
        expect(searchResultsText).toContain(`Woody, Buzz and the whole gang are back. As their owner Andy prepares to depart for college, his loyal toys find themselves in daycare where untamed tots with their sticky little fingers do not play nice. So, it's all for one and one for all as they join Barbie's counterpart Ken, a thespian hedgehog named Mr. Pricklepants and a pink, strawberry-scented teddy bear called Lots-o'-Huggin' Bear to plan their great escape.`);
        expect(searchResultsText).toContain(`Tom Hanks`);
        expect(searchResultsText).toContain(`Woody (voice)`);
    });
});
