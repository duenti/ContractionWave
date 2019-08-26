package model;

import static org.bytedeco.javacpp.opencv_imgcodecs.imread;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.bytedeco.javacpp.opencv_core.Mat;


public class ImageGroup extends Group{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<File> images;
	private String sizecol;
	private int width, height;
	
	private static final DecimalFormat format = new DecimalFormat("#.##");
	private static final double GiB = 1024 * 1024 * 1024;
	private static final double MiB = 1024 * 1024;
	private static final double KiB = 1024.0;
	
	public String convertFileSize(long length) {
		double length2 = Double.valueOf(length);
	    if (length2 > GiB) {
	        return format.format(length / GiB) + " GiB";
	    }
	    if (length2 > MiB) {
	        return format.format(length / MiB) + " MiB";
	    }
	    if (length2 > KiB) {
	        return format.format(length / KiB) + " KiB";
	    }
	    return format.format(length) + " B";
	}
	
	private String sorttype = "";
	
    public void setSorttype(String value) {
        sorttype = value;
    }

    public String getSorttype() {
        return sorttype;
    }
	
	public ImageGroup(String name, String path){
		super(name,path,1);
		images = new ArrayList<File>();
		long totalFileSize = 0;
		File directory = new File(path);
		for(File file : directory.listFiles()){
			String filename = file.getName();
			if (filename != null && filename.matches(".*((\\.[jJ][pP][gG])|(\\.[jJ][pP][eE][gG])|(\\.[tT][iI][fF])|(\\.[tT][iI][fF][fF])|(\\.[pP][nN][gG]))")) {
				images.add(file);
				totalFileSize += file.length();
			}
		}
		this.sortByNumbers();
		this.setSizecol(convertFileSize(totalFileSize));
		if(images.size()>0){
			Mat m = imread(images.get(0).getAbsolutePath());
			setWidth(m.cols());
			setHeight(m.rows());
		}
		//extractMatrices();
		
	}
	public ImageGroup(String name, List<String> paths){
		super(name,paths,1);
		images = new ArrayList<File>();
		for(String path : paths){
			File directory = new File(path);
			for(File file : directory.listFiles()){
				String filename = file.getName();
				if (filename != null && filename.matches(".*((\\.[jJ][pP][gG])|(\\.[jJ][pP][eE][gG])|(\\.[tT][iI][fF])|(\\.[tT][iI][fF][fF])|(\\.[pP][nN][gG]))")) {
					images.add(file);
				}
			}
		}
		if(images.size()>0){
			Mat m = imread(images.get(0).getAbsolutePath());
			setWidth(m.cols());
			setHeight(m.rows());
		}
		this.sortByNumbers();
		//extractMatrices();
	}
	
	public void sortByNumbers(){
		this.setSorttype("Numbers");
		Comparator<File> cmp = new Comparator<File>() {
		    public int compare(File o1, File o2) {
		    	int n1 = extractNumber(o1.getName());
				int n2 = extractNumber(o2.getName());
				return n1 - n2;
		    }
		    private int extractNumber(String name) {
				int i = 0;
				try {
					String num = name.replaceAll("[^\\d]", "");
					i = Integer.parseInt(num);
				} catch(Exception e) {
					i = 0; // if filename does not match the format
					// then default to 0
				}
				return i;
			}
	    };
	    Collections.sort(images, cmp);
	}
	
	public void sortByLastModifiedDate(){
		this.setSorttype("Modified Date");
		Collections.sort(images,LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
	}

	private final boolean isDigit(char ch)
    {
        return ((ch >= 48) && (ch <= 57));
    }
	private final String getChunk(String s, int slength, int marker)
    {
        StringBuilder chunk = new StringBuilder();
        char c = s.charAt(marker);
        chunk.append(c);
        marker++;
        if (isDigit(c))
        {
            while (marker < slength)
            {
                c = s.charAt(marker);
                if (!isDigit(c))
                    break;
                chunk.append(c);
                marker++;
            }
        } else
        {
            while (marker < slength)
            {
                c = s.charAt(marker);
                if (isDigit(c))
                    break;
                chunk.append(c);
                marker++;
            }
        }
        return chunk.toString();
    }
	public void sortByAlphanumericName(){
		this.setSorttype("Alphanumeric");
		Comparator<File> cmp = new Comparator<File>() {
		    public int compare(File o1, File o2) {
		    	String s1 = o1.getName();
		    	String s2 = o2.getName();
		    	if ((s1 == null) || (s2 == null)) 
		    	{
		    		return 0;
		    	}

		        int thisMarker = 0;
		        int thatMarker = 0;
		        int s1Length = s1.length();
		        int s2Length = s2.length();

		        while (thisMarker < s1Length && thatMarker < s2Length)
		        {
		            String thisChunk = getChunk(s1, s1Length, thisMarker);
		            thisMarker += thisChunk.length();

		            String thatChunk = getChunk(s2, s2Length, thatMarker);
		            thatMarker += thatChunk.length();

		            // If both chunks contain numeric characters, sort them numerically
		            int result = 0;
		            if (isDigit(thisChunk.charAt(0)) && isDigit(thatChunk.charAt(0)))
		            {
		                // Simple chunk comparison by length.
		                int thisChunkLength = thisChunk.length();
		                result = thisChunkLength - thatChunk.length();
		                // If equal, the first different number counts
		                if (result == 0)
		                {
		                    for (int i = 0; i < thisChunkLength; i++)
		                    {
		                        result = thisChunk.charAt(i) - thatChunk.charAt(i);
		                        if (result != 0)
		                        {
		                            return result;
		                        }
		                    }
		                }
		            } 
		            else
		            {
		                result = thisChunk.compareTo(thatChunk);
		            }

		            if (result != 0)
		                return result;
		        }

		        return s1Length - s2Length;
		    }
	    };
	    Collections.sort(images, cmp);
	}
	
	public void reverseImages(){
		this.setSorttype("Reverse " + this.getSorttype());
		Collections.reverse(images);
	}
	
	public void removeImageByIndex(int i){
		if(images.size() < i){
			images.remove(i);
		}
	}

	public List<File> getImages(){
		return images;
	}
	
	public List<File> getImageSubset(int fromIndex, int toIndex){
		return images.subList(fromIndex, toIndex);
	}
	
	public void setParameters(double pyrScale, int levels, int winSize, int iterations, int polyN, double polySigma) {
		this.pyrScale = pyrScale;
		this.levels = levels;
		this.winSize = winSize;
		this.iterations = iterations;
		this.polyN = polyN;
		this.polySigma = polySigma;
	}
	public int size() {
		return images.size();
	}
	public int getSize(){
		return images.size();
	}
	public String getSizecol() {
		return sizecol;
	}
	public void setSizecol(String sizecol) {
		this.sizecol = sizecol;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public boolean changeImagePath(String directory){
		List<File> newImages = new ArrayList<File>();
		
		for(File f : images){
			String filename = f.getName();
			String newFilename = directory + File.separator + filename;
			File f2 = new File(newFilename);
			if(f2.exists()){
				newImages.add(f2);
				System.out.println(newFilename);
			}else{
				errorMessage = newFilename;
				return false;
			}
		}
		
		images = newImages;
		paths.clear();
		paths.add(directory);
		return true;
	}
}