# SITE-REVAMP

Site service provides an API for reading and updating site data. The sites are localities of interest related to research activities (e.g from the FRED and Petlab databases). Records contain both coordinates and metadata.

The service is implemented as a Spring Boot application.

# Deployment

## Staging deployment
Builds a docker image from the current branches source, then creates a container from that image in the GNS Web Application Staging portainer environment (or replaces it if it already exists).


## Production deployment
Does a blue-green deployment to ensure the end users are not affected, via the following steps: 
- Creates a docker image containing the latest version of site service from the gns-libs-release maven repository
- Creates a New_Site_Api-xxxx (where xxxx is a port number) container from that image in the GNS Web Applications portainer environment
- Waits until the site service is accessible in the new container
- Removes the previous New_Site_Api-xxxx container (if it exists)