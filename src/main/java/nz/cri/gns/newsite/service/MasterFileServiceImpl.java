package nz.cri.gns.newsite.service;

import java.util.logging.Level;
import java.util.logging.Logger;
import nz.cri.gns.newsite.model.SiteModel;
import nz.cri.gns.newsite.utils.CoordinateConverter;
import org.locationtech.jts.geom.Geometry;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 *
 * @author sorenh
 */
@Service
@Scope("singleton")
@Configurable
public class MasterFileServiceImpl implements MasterFileService {

    public static final int MASTER_FILE_EPSG = 27200;

    public static final int REG_MAINLAND_NZ = 400;
    public static final int REG_CHATHAM_ISLANDS = 401;
    public static final int REG_ROSS_SEA = 402;
    public static final int REG_NEW_CALEDONIA = 403;
    public static final int REG_TOKELAU = 404;
    public static final int REG_FIJI = 405;
    public static final int REG_SAMOA = 406;
    public static final int REG_NIUE = 407;
    public static final int REG_COOK_ISLANDS = 408;
    public static final int REG_NORFOLK_ISLAND = 409;
    public static final int REG_TONGA = 410;
    public static final int REG_LORD_HOWE_ISLAND = 411;
    public static final int REG_KERMADEC_ISLANDS = 412;
    public static final int REG_BOUNTY_ISLANDS = 413;
    public static final int REG_THE_SNARES = 414;
    public static final int REG_CAMPBELL_ISLAND = 415;
    public static final int REG_AUCKLAND_ISLANDS = 416;
    public static final int REG_ANTIPODES_ISLANDS = 417;
    public static final int REG_MACQUARIE_ISLAND = 418;
    public static final int REG_OTHER = 419;
    public static final int REG_VANUATU = 420;
    public static final int REG_PAPUA_NEW_GUINEA = 421;
    public static final int MASTERFILE_NTH_NI = 1;
    public static final int MASTERFILE_CEN_NI = 2;
    public static final int MASTERFILE_STH_NI = 3;
    public static final int MASTERFILE_NELSON = 4;
    public static final int MASTERFILE_CEN_SI = 5;
    public static final int MASTERFILE_STH_SI = 6;
    public static final int MASTERFILE_NZ_ISLANDS = 7;
    public static final int MASTERFILE_ANTARCTICA = 8;
    public static final int MASTERFILE_PACIFIC_ISLANDS = 9;
    public static final int MASTERFILE_NEW_CALEDONIA = 10;
    public static final int MASTERFILE_OFFSHORE = 11;
    //This is a special backlog masterfile folder
    public static final int MASTERFILE_NTH_NI_BACKLOG = 14;
    public static final int MASTERFILE_CEN_NI_BACKLOG = 17;
    public static final int MASTERFILE_STH_NI_BACKLOG = 19;
    public static final int MASTERFILE_NELSON_BACKLOG = 12;
    public static final int MASTERFILE_CEN_SI_BACKLOG = 20;
    public static final int MASTERFILE_STH_SI_BACKLOG = 22;
    public static final int MASTERFILE_NZ_ISLANDS_BACKLOG = 23;
    public static final int MASTERFILE_ANTARCTICA_BACKLOG = 24;
    public static final int MASTERFILE_PACIFIC_ISLANDS_BACKLOG = 25;
    public static final int MASTERFILE_NEW_CALEDONIA_BACKLOG = 26;
    public static final int MASTERFILE_OFFSHORE_BACKLOG = 27;

    /**
     * Retrieves the MasterFile from a basic coordinate lookup. Consumed by
     * FRED. Coordinates are in NZMG / EPSG:27200.
     *
     * @param site
     * @param registrationAreaId
     * @param isBacklog
     * @return
     */
    @Override
    public Integer getMasterFile(SiteModel site, int registrationAreaId, boolean isBacklog) {

        try {
            switch (registrationAreaId) {
                case REG_MAINLAND_NZ:
                    Geometry location = CoordinateConverter.convertGeometryCoordinates(site.getShape(), SiteModel.SITE_EPSG, MASTER_FILE_EPSG);
                    double easting = location.getCoordinate().getX();
                    double northing = location.getCoordinate().getY();
                    if (easting <= 2810000 && northing >= 6250000) {
                        return (isBacklog) ? MASTERFILE_NTH_NI_BACKLOG : MASTERFILE_NTH_NI;
                    }
                    if (northing >= 6160000 || (easting >= 2730000 && northing >= 6070000)) {
                        return (isBacklog) ? MASTERFILE_CEN_NI_BACKLOG : MASTERFILE_CEN_NI;
                    }
                    if (easting >= 2650000 || northing >= 6130000) {
                        return (isBacklog) ? MASTERFILE_STH_NI_BACKLOG : MASTERFILE_STH_NI;
                    }
                    if (northing >= 5920000) {
                        return (isBacklog) ? MASTERFILE_NELSON_BACKLOG : MASTERFILE_NELSON;
                    }
                    if (easting >= 2210000 && northing >= 5620000) {
                        return (isBacklog) ? MASTERFILE_CEN_SI_BACKLOG : MASTERFILE_CEN_SI;
                    }
                    if ((northing >= 5290000)) {
                        return (isBacklog) ? MASTERFILE_STH_SI_BACKLOG : MASTERFILE_STH_SI;
                    }
                    return (isBacklog) ? MASTERFILE_OFFSHORE_BACKLOG : MASTERFILE_OFFSHORE;
                case REG_CHATHAM_ISLANDS:
                case REG_CAMPBELL_ISLAND:
                case REG_AUCKLAND_ISLANDS:
                case REG_ANTIPODES_ISLANDS:
                case REG_THE_SNARES:
                    return (isBacklog) ? MASTERFILE_NZ_ISLANDS_BACKLOG : MASTERFILE_NZ_ISLANDS;
                case REG_ROSS_SEA:
                    return (isBacklog) ? MASTERFILE_ANTARCTICA_BACKLOG : MASTERFILE_ANTARCTICA;
                case REG_TOKELAU:
                case REG_FIJI:
                case REG_SAMOA:
                case REG_NIUE:
                case REG_COOK_ISLANDS:
                case REG_NORFOLK_ISLAND:
                case REG_TONGA:
                case REG_LORD_HOWE_ISLAND:
                case REG_KERMADEC_ISLANDS:
                case REG_BOUNTY_ISLANDS:
                case REG_MACQUARIE_ISLAND:
                case REG_VANUATU:
                case REG_PAPUA_NEW_GUINEA:
                    return (isBacklog) ? MASTERFILE_PACIFIC_ISLANDS_BACKLOG : MASTERFILE_PACIFIC_ISLANDS;
                case REG_NEW_CALEDONIA:
                    return (isBacklog) ? MASTERFILE_NEW_CALEDONIA_BACKLOG : MASTERFILE_NEW_CALEDONIA;
                case REG_OTHER:
                    return (isBacklog) ? MASTERFILE_OFFSHORE_BACKLOG : MASTERFILE_OFFSHORE;
            }
            return (isBacklog) ? MASTERFILE_OFFSHORE_BACKLOG : MASTERFILE_OFFSHORE;
        } catch (TransformException | FactoryException ex) {
            Logger.getLogger(MasterFileServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
