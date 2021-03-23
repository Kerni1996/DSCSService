
package Helper;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;

/**
 * code from https://github.com/msteiger/jxmapviewer2
 * A waypoint that also has a color and a label
 * @author Martin Steiger
 */
public class MyWaypoint extends DefaultWaypoint
{
    private final String label;
    private final Color color;

    /**
     * @param label the text
     * @param color the color
     * @param coord the coordinate
     */
    public MyWaypoint(String label, Color color, GeoPosition coord)
    {
        super(coord);
        this.label = label;
        this.color = color;
    }

    /**
     * @return the label text
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * @return the color
     */
    public Color getColor()
    {
        return color;
    }

}
