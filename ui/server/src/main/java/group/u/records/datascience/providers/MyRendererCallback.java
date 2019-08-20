package group.u.records.datascience.providers;

import org.sweble.wikitext.engine.PageTitle;
import org.sweble.wikitext.engine.output.HtmlRendererCallback;
import org.sweble.wikitext.engine.output.MediaInfo;
import org.sweble.wikitext.parser.nodes.WtUrl;

public class MyRendererCallback implements HtmlRendererCallback {
    @Override
    public MediaInfo getMediaInfo(String title, int width, int height) throws Exception {
        return null;
    }

    @Override
    public boolean resourceExists(PageTitle target) {
        return false;
    }

    @Override
    public String makeUrl(PageTitle linkTarget) {
        return null;
    }

    @Override
    public String makeUrl(WtUrl target) {
        return null;
    }

    @Override
    public String makeUrlMissingTarget(String path) {
        return null;
    }
}
