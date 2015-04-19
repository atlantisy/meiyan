package cn.minelock.widget;

import java.util.ArrayList;  
import java.util.List;  
  
import android.graphics.Bitmap;  
  
public class ImageSplitter {  
  
    public static List<ImagePiece> split(Bitmap bitmap, int xPiece, int yPiece) {  
  
        List<ImagePiece> pieces = new ArrayList<ImagePiece>(xPiece * yPiece);  
        int width = bitmap.getWidth();  
        int height = bitmap.getHeight();  
        int pieceWidth = width / 3;  
        int pieceHeight = height / 3;  
        for (int i = 0; i < yPiece; i++) {  
            for (int j = 0; j < xPiece; j++) {  
                ImagePiece piece = new ImagePiece();  
                piece.index = j + i * xPiece;  
                int xValue = j * pieceWidth;  
                int yValue = i * pieceHeight;  
                piece.bitmap = Bitmap.createBitmap(bitmap, xValue, yValue,  
                        pieceWidth, pieceHeight);  
                pieces.add(piece);  
            }  
        }  
  
        return pieces;  
    }  
  
}  