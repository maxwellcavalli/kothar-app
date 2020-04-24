package br.com.kotar.core.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;

public class ImageUtil {

	
	public static byte[] gerarThumb(byte[] imagemOriginal, int altura, int largura) {
		
		byte[] retorno = null;
		
		ImageIcon icon = new ImageIcon(imagemOriginal); 
		
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();
        float scale = Math.max(width / (float) largura, height / (float) altura); 
        if (scale > 1) {
        	icon.setImage(icon.getImage().getScaledInstance((int) (width / scale), (int) (height / scale), 100));
        }
        else {
        	icon.setImage(icon.getImage().getScaledInstance(largura, altura, 100));	
        }
		
		BufferedImage img = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		icon.paintIcon(null, g2d, 0, 0);
		g2d.dispose();

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
		    ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
		    try {
		        ImageIO.write(img, "png", ios);
		        // Set a flag to indicate that the write was successful
		    } finally {
		        ios.close();
		    }
		    retorno = baos.toByteArray();
		} catch (IOException ex) {
		    ex.printStackTrace();
		}
		
		return retorno;
	}
}

