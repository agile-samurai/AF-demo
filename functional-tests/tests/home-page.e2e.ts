import { HomePage } from '../page-objects/home-page.po';

describe(`Feature: Navigate to the home page`, async () => {
    let homePage: HomePage;

    beforeAll(async () => {
        homePage = new HomePage();
        await homePage.waitForApplication();
    });

    it('I should confirm the body is present', async () => {
        const body = await homePage.waitForPresenceAndGetBody();

        const bodyPresence = await body.isPresent();

        expect(bodyPresence).toBe(true);
    });
});
