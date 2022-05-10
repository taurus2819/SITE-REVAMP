package nz.cri.gns.newsite.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author sorenh
 */
@Getter @AllArgsConstructor
public class MapSheetPayload {
    private String name;
    private int bboxEast;
    private int bboxWest;
    private int bboxSouth;
    private int bboxNorth;
}
