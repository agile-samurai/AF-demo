const {SpecReporter} = require('jasmine-spec-reporter');

baseConfigObject = {
    directConnect: true,

    useAllAngular2AppRoots: true,

    SELENIUM_PROMISE_MANAGER: false,

    framework: 'jasmine2',

    specs: [
        './tests/home-page.e2e.ts',
    ],

    capabilities: {
        browserName: 'chrome',
        chromeOptions: {
            args: ['--window-size=1340,943', '--allow-insecure-localhost', '--ignore-certificate-errors',
                'no-sandbox', 'headless', 'disable-gpu']
        }
    },

    jasmineNodeOpts: {
        showTiming: true,
        showColors: true,
        isVerbose: true,
        includeStackTrace: true,
        defaultTimeoutInterval: 400000
    },

    onPrepare: function () {
        require('ts-node').register({
            project: './tsconfig.json'
        });
        browser.waitForAngularEnabled(false);
        browser.manage().deleteAllCookies();
        jasmine.getEnv().addReporter(new SpecReporter({spec: {displayStacktrace: true}}));
    },
};

exports.config = baseConfigObject;