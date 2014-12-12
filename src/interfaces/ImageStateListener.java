package interfaces;

import java.awt.image.BufferedImage;

public interface ImageStateListener {
	public void updateImage(BufferedImage image);
	public void updateImageSize(int w, int h);
}
