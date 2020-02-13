package de.uros.citlab.module.htr;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uros.citlab.module.htr.HTRParserPlus;
import de.uros.citlab.module.kws.ConfMatContainer;
import de.uros.citlab.module.types.HTR;
import de.uros.citlab.module.types.Key;
import de.uros.citlab.module.util.PropertyUtil;
import org.junit.Assert;

public class HTRParserPlusTest {
	private static final Logger LOG = LoggerFactory.getLogger(HTRParserPlusTest.class);
	
	/**
	 * Test for the fix of https://github.com/Transkribus/TranskribusAppServerModules/issues/76
	 * <br><br>
	 * HTR was cached for each processed page as the confmat container filename property was taken into account for the hashCode i.e. key in the HTR cache map.
	 * This caused the cache grow unnecessarily and made the HTR crash with OutOfMemoryError, especially if a ILangMod was included.
	 */
	@Test 
	public void testExtractHtrProperties() {
		final String om = "/path/to/HTR";
		final String lm = "/path/to/dict.dict";
		final String cm = "/path/to/HTR" + Key.GLOBAL_CHARMAP;
		String[] props = new String[] {"mySpecialParam", "DO NOT TOUCH"};
		
		final HashMap<Integer, HTR> htrs = new HashMap<>();
		
		for(int i = 0; i < 10; i++) {
			final String confMatFileName = FilenameUtils.getBaseName(ConfMatContainer.CONFMAT_CONTAINER_FILENAME)
					+ "_" + i + "." + FilenameUtils.getExtension(ConfMatContainer.CONFMAT_CONTAINER_FILENAME);
			props = PropertyUtil.setProperty(props, Key.HTR_CONFMAT_CONTAINER_FILENAME, confMatFileName);
		
			//remove the Confmat filename as we do not want that in the HTR cache
			String[] htrProps = HTRParserPlus.extractHtrProperties(props);
			LOG.info("Initial props after extraction: {}", Arrays.stream(props).collect(Collectors.joining(", ", "[ ", " ]")));
			
			//this is the caching mechanism's code from HTRParser(Plus)
			HTR htrDummy = new HTR(om, lm, cm, htrProps);
			int hash = htrDummy.hashCode();
	        if (!htrs.containsKey(hash)) {
	        	htrs.put(hash, htrDummy);
	        }
		}
		Assert.assertEquals("HTR cache contains the same HTR multiple times!", 1, htrs.size());
		LOG.info("HTR cache size = {}", htrs.size());
	}
}
