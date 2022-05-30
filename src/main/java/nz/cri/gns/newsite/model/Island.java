package nz.cri.gns.newsite.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;

/**
 *
 * @author sorenh
 */
@Entity
@Table(name = "island", schema = "sc")
@TypeDefs({
    @TypeDef(name = "json", typeClass = JsonBinaryType.class)
})
@Getter
public class Island implements Serializable {
    
    @Column(name = "island_id", updatable = false)
    @NotNull
    @Id
    private Integer islandId;

    @Column(name = "name")
    private String name;
    
    @Column(name = "country_code")
    private String countryCode;
    
    @Column(name = "top")
    private double bboxTop;
    
    @Column(name = "bottom")
    private double bboxBottom;
    
    @Column(name = "left")
    private double bboxLeft;
    
    @Column(name = "right")
    private double bboxRight;
    
    @JsonIgnore
    public Geometry getBBox()   {
        final GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), getDefaultEPSG());
            Polygon polygon = factory.createPolygon(factory.createLinearRing(new Coordinate[]{
                new Coordinate(bboxLeft, bboxBottom),
                new Coordinate(bboxLeft, bboxTop),
                new Coordinate(bboxRight, bboxTop),
                new Coordinate(bboxRight, bboxBottom),
                new Coordinate(bboxLeft, bboxBottom),
            }), null);   
            return polygon;
    }
    
    /**
     * NZMG
     * @return EPSG code
     */
    public static int getDefaultEPSG() {
       return 4326;
    } 

}
