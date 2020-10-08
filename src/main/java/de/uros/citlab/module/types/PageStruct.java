package de.uros.citlab.module.types;

import de.uros.citlab.module.util.PageXmlUtil;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.interfaces.types.Image;

import java.io.File;
import java.net.MalformedURLException;

/**
 * Container for pathes to image and xml. Both, The structure can load the
 * image. While the image is read-only, the xml-file can be loaded and saved.
 *
 * @author gundram
 */
public class PageStruct {

    private PcGtsType xmlFile;
    private final File pathXml;
    private Image img;
    private final File pathImg;
    private IImageFactory imageFactory; 

    public PageStruct(File pathXml, File pathImg, IImageFactory imageBuilder) {
		this(pathXml, pathImg, true, imageBuilder);
    }
    public PageStruct(File pathXml, File pathImg, boolean forceExistance, IImageFactory imageBuilder) {
        if (pathImg == null && pathXml == null) {
            throw new RuntimeException("path to image and xml is null");
        }
        this.pathXml = pathXml != null ? pathXml : PageXmlUtil.getXmlPath(pathImg, forceExistance);
        this.pathImg = pathImg != null ? pathImg : PageXmlUtil.getImagePath(pathXml,forceExistance);
		setImageFactory(imageBuilder);
    }

    public PageStruct(File xmlOrImgFile, IImageFactory imageBuilder) {
        if (xmlOrImgFile.getName().toLowerCase().endsWith(".xml")) {
            pathXml = xmlOrImgFile;
            pathImg = PageXmlUtil.getImagePath(xmlOrImgFile, true);
        } else {
            pathImg = xmlOrImgFile;
            pathXml = PageXmlUtil.getXmlPath(xmlOrImgFile, true);
        }
        setImageFactory(imageBuilder);
    }
    
	private void setImageFactory(IImageFactory imageFactory) {
		if(imageFactory == null) {
			imageFactory = new DefaultImageFactory();
		}
		this.imageFactory = imageFactory;
	}
	
	public Image getImg() {
        if (img == null) {
	    	try {
	        	img = imageFactory.create(pathImg.toURI().toURL());
	        } catch (MalformedURLException ex) {
				throw new RuntimeException(ex);
			}
        }
        return img;
    }
	
	public void saveXml() {
        PageXmlUtil.marshal(getXml(), pathXml);
    }

    public PcGtsType getXml() {
        if (xmlFile == null) {
            xmlFile = PageXmlUtil.unmarshal(pathXml);
        }
        return xmlFile;
    }

    public File getPathImg() {
        return pathImg;
    }

    public File getPathXml() {
        return pathXml;
    }

}
