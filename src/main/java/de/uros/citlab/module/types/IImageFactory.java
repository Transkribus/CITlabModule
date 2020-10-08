package de.uros.citlab.module.types;

import java.net.URL;

import eu.transkribus.interfaces.types.Image;

/**
 * Factory for {@link Image} objects.
 */
public interface IImageFactory {
	/**
	 * Instantiate an {@link Image} by {@link URL}.
	 * 
	 * @param imgUrl URL referring to the image file
	 * @return instantiated Image object
	 */
	public Image create(URL imgUrl);
}
