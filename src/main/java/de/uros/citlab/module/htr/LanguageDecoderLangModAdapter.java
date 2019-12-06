//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package de.uros.citlab.module.htr;

import com.achteck.misc.param.ParamSet;
import com.achteck.misc.types.ConfMat;
import com.achteck.misc.types.ParamTreeOrganizer;
import de.planet.citech.types.IDecodingType;
import de.planet.citech.types.ISortingFunction;
import de.planet.imaging.types.IWDImage;
import de.planet.itrtech.types.IDictOccurrence;
import de.planet.itrtech.writingdecoder.IWritingDecoder;
import de.planet.langmod.types.ILangMod;
import de.planet.languagemodel.beamsearch.CTCBeamSearch;
import de.planet.languagemodel.beamsearch.type.LMCostMapperChar;
import de.planet.languagemodel.decoder.LanguageDecoder;
import de.planet.languagemodel.decoder.LanguageDecoderLM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

public class LanguageDecoderLangModAdapter extends ParamTreeOrganizer implements ILangMod {
    private final LanguageDecoder languageDecoder;
    private ConfMat confMat;
    private ISortingFunction isf;
    Logger LOG = LoggerFactory.getLogger(LanguageDecoderLangModAdapter.class);

    public LanguageDecoderLangModAdapter(String pathToLanguageResource, String spaceSubs, double alpha, double beta, double nacoffset) {
        this.languageDecoder = new LanguageDecoderLM();
        ParamSet ps = this.languageDecoder.getDefaultParamSet((ParamSet) null);
        ps.setParam("ctcbeamsearchclass", CTCBeamSearch.class.getCanonicalName());
        ps.setParam("ctcbeamsearchclass/costThreshold", -Math.log(1.0E-4D));
        ps.setParam("ctcbeamsearchclass/languageModelClass", LMBerkleyChar.class.getCanonicalName());
        ps.setParam("ctcbeamsearchclass/languageModelClass/path2File", pathToLanguageResource);
        ps.setParam("ctcbeamsearchclass/languageModelClass/spaceSubs", spaceSubs);
        ps.setParam("ctcbeamsearchclass/lmCostMapperClass", LMCostMapperChar.class.getCanonicalName());
        ps.setParam("ctcbeamsearchclass/lmCostMapperClass/alpha", alpha);
        ps.setParam("ctcbeamsearchclass/lmCostMapperClass/beta", beta);
        ps.setParam("ctcbeamsearchclass/withBoundaryCosts", false);
        ps.setParam("ctcbeamsearchclass/appendSpace", false);
        ps.setParam("ctcbeamsearchclass/nacOffset", nacoffset);
        ps.setParam("ctcbeamsearchclass/numberOfToks2Keep", 100);
        this.languageDecoder.setParamSet(ps);
        this.languageDecoder.init();
    }

    @Override
    public void setConfMat(ConfMat cm) {
        this.confMat = cm;
    }

    @Override
    public void setDict(String string, Collection<String> clctn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDict(String string, IDictOccurrence ido) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeDict(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setInput(IWDImage iwdi) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update() {
    }

    @Override
    public void setConfMats(List<ConfMat> list) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ISortingFunction getSortingFunction() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setSortingFunction(ISortingFunction isf) {
        this.isf = isf;
    }

    @Override
    public ILangModResult getResult() {
        final IDecodingType s = this.languageDecoder.decode(this.confMat);
        return new ILangModResult() {
            @Override
            public String getText() {
                return s.getText();
            }

            @Override
            public double getCostAbs() {
                return s.getCostAbs();
            }

            @Override
            public double getScore() {
                return LanguageDecoderLangModAdapter.this.isf.getSortingCost(s);
            }

            @Override
            public String getBestPath() {
                return LanguageDecoderLangModAdapter.this.confMat.getBestPath();
            }

            @Override
            public List<? extends ILangModGroup> getGroups() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public List<? extends ILangModGroup> getGroups(String string) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    @Override
    public List<ILangModResult> getResults() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IWritingDecoder getWritingDecoder() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setWritingDecoder(IWritingDecoder iwd) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
