package group.u.mdas.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CompanyIdentifierTest {

    public static final String INDUSTRY = "Industry";
    public static final String SECTOR = "Sector";
    public static final String NAME = "Name";
    public static final String SYMB = "SYMB";

    @Test
    public void testWillBindCorrectly(){
        CompanyIdentifier companyIdentifier = new CompanyIdentifier(SYMB, NAME, SECTOR, INDUSTRY);

        assertThat(companyIdentifier.getIndustry()).isEqualTo(INDUSTRY);
        assertThat(companyIdentifier.getSector()).isEqualTo(SECTOR);
        assertThat(companyIdentifier.getSymbol()).isEqualTo(SYMB);
        assertThat(companyIdentifier.getName()).isEqualTo(NAME);
    }

}
