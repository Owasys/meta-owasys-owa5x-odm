SUMMARY = "Recipes needed to deploy the Owasys Device Manager on an owa5X"

PACKAGE_ARCH = "${MACHINE_ARCH}"
inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = " packagegroup-odm \
"
RDEPENDS:${PN} = "\
    paho-mqtt-c \
    owasys-polluxc-mqtt \
    rauc \
    casync \
    owasysd-boot-check \
    odm-agent \
    odm-remote-shell \
"
