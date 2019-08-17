package group.u.records.ds.providers;

import de.fau.cs.osr.ptk.common.jxpath.AstNodePointerFactory;
import group.u.records.models.entity.MovieDetail;
import group.u.records.service.Lineage;
import group.u.records.service.MovieDetailsDataSource;
import group.u.records.service.S3DataService;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.sweble.wikitext.engine.EngineException;
import org.sweble.wikitext.engine.PageId;
import org.sweble.wikitext.engine.PageTitle;
import org.sweble.wikitext.engine.WtEngineImpl;
import org.sweble.wikitext.engine.config.WikiConfig;
import org.sweble.wikitext.engine.nodes.EngProcessedPage;
import org.sweble.wikitext.engine.output.HtmlRenderer;
import org.sweble.wikitext.engine.utils.DefaultConfigEnWp;
import org.sweble.wikitext.parser.nodes.WtNode;
import org.sweble.wikitext.parser.nodes.WtParagraph;
import org.sweble.wikitext.parser.parser.LinkTargetException;
import org.sweble.wikitext.parser.utils.WtRtDataPrinter;

import java.io.IOException;
import java.util.Iterator;

public class WikipediaMoveDataSource extends MovieDetailsDataSource {
    private String folder;
    private S3DataService dataService;
    private Logger logger = LoggerFactory.getLogger(WikipediaMoveDataSource.class);

    public WikipediaMoveDataSource() {
        super(Lineage.WIKIPEDIA);
    }

    public WikipediaMoveDataSource(@Value("${aws.folder.wikipedia}") String folder,
                                   S3DataService dataService) {
        super(Lineage.WIKIPEDIA);
        this.folder = folder;
        this.dataService = dataService;
    }

    @Override
    public MovieDetail getMovieDetails(String id) {
        try {
            dataService.getFileAsString(folder + "/" + id + ".wiki");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MovieDetail parseAsString(String wikiMarkdown) {
        return null;
    }

    static
    {
        JXPathContextReferenceImpl.addNodePointerFactory(
                new AstNodePointerFactory());
    }

    public void getCharacters(String id, String text ) {
// Set-up a simple wiki configuration

//        engine.parse(null, "", )

        // Set-up a simple wiki configuration
        WikiConfig config = DefaultConfigEnWp.generate();
        // Instantiate a compiler for wiki pages
        WtEngineImpl engine = new WtEngineImpl(config);
        // Retrieve a page
        PageTitle pageTitle = null;
        try {
            pageTitle = PageTitle.make(config, "fake");
        } catch (LinkTargetException e) {
            e.printStackTrace();
        }
        PageId pageId = new PageId(pageTitle, -1);
        // Compile the retrieved page
        EngProcessedPage cp = null;
        try {
            cp = engine.postprocess(pageId, text, null);
            String ourHtml = HtmlRenderer.print(new MyRendererCallback(), config, pageTitle, cp.getPage());
            logger.debug(ourHtml);

            JXPathContext context = JXPathContext.newContext(cp.getPage());
            Iterator results = context.iterate("*");

            while(results.hasNext()){
                WtParagraph next = (WtParagraph) results.next();
                WtNode infoBox = ((WtNode) next).get(2);
//                for( Object field : infoBox.getRtd().getFields() ){
//                    logger.debug("field:  " + field. );
//                }
                logger.debug( "Special node: " + WtRtDataPrinter.print(infoBox));
                logger.debug("Result:  " + WtRtDataPrinter.print(next));
            }
        } catch (EngineException e) {
            e.printStackTrace();
        }

//        logger.debug( "Child named:  " + cp.getPage().getChildNames());

        logger.debug("Node name:  " + cp.getPage());
        logger.debug("Node count:  " + cp.getPropertyCount());
        logger.debug("Character Property:  " + cp.getPage().getProperty("Characters"));
        for( String str :  cp.getPage().getChildNames()){
            logger.debug("name:  " + str );
        }

        // Retrieve a page
//        PageTitle pageTitle = PageTitle.make(config, fileTitle);
    }
}
