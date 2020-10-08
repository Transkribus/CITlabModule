package de.uros.citlab.module.types;

import java.net.URL;

import de.planet.imaging.types.HybridImage;
import eu.transkribus.interfaces.types.Image;


/**
 * {@link IImageFactory} implementation specifying the default CITlab routine for instantiating images using {@link HybridImage}.<br>
 * This implementation will <i>not</i> respect the image orientation information stored in the EXIF data.
 */
public class DefaultImageFactory implements IImageFactory {
	@Override
	public Image create(URL imgUrl) {
		return new Image(HybridImage.newInstance(imgUrl).getAsOpenCVMatImage());
	}
}
