import { CelebrityPage } from '../../page-objects/celebrity-page.po';
import { HomePage } from '../../page-objects/dossiers-page.po';
import { LoginPage } from '../../page-objects/login-page.po';

describe(`Feature: Land on the celebrity page`, async () => {
    let homePage: HomePage;
    let loginPage: LoginPage;
    let celebrityPage: CelebrityPage;

    beforeAll(async () => {
        homePage = new HomePage();
        loginPage = new LoginPage();
        celebrityPage = new CelebrityPage();

        await celebrityPage.waitForApplication();
        await loginPage.login(`business-user`, `password`);
    });

    it(`I confirm the search bar is present`, async () => {
        const searchBar = await celebrityPage.waitForPresenceAndGetSearchBar();

        const searchBarPresence = await searchBar.isPresent();

        expect(searchBarPresence).toBe(true);
    });
});
