# A minimal demo bundle
#
# Note: The created bundle will not contain RAUC itself yet!
# To add this, properly configure it for your specific system and add it to
# your image recipe you intend to build a bundle from:
#
# IMAGE_INSTALL:append = " rauc"
#
# Also note that you need to configure RAUC_KEY_FILE and RAUC_CERT_FILE to
# point to contain the full path to your key and cert.
# Depending on you requirements you can either set them via global
# configuration or from a bundle recipe bbappend.
#
# For testing purpose, you may use the scripts/openssl-ca.sh to create some.
LICENSE = "Proprietary"
LIC_FILES_CHKSUM ="file://${COMPANY_CUSTOM_LICENSES}/OWASYS_Propietary_SW_License_Agreement.md;md5=203a753c44e11367199c31c2168fa959"

inherit bundle

SRC_URI:append = "file://upload-bundle"

BUNDLE_BASENAME ?= "${MACHINE}-${OS_ID}-${DISTRO_VERSION}-${ODM_APP_NAME}-${ODM_APP_VERSION}"
BUNDLE_NAME ?= "${BUNDLE_BASENAME}-${DATETIME}"
BUNDLE_LINK_NAME ?= "${BUNDLE_BASENAME}"
CASYNC_BUNDLE_NAME ?= "${BUNDLE_BASENAME}-${DATETIME}"
CASYNC_BUNDLE_LINK_NAME ?= "${BUNDLE_BASENAME}"

RAUC_BUNDLE_COMPATIBLE ?= "owa5"
RAUC_BUNDLE_FORMAT = "verity"
RAUC_BUNDLE_SLOTS ?= "rootfs"
RAUC_BUNDLE_VERSION ?= "${BUNDLE_BASENAME}"

FSTYPE = "${@ "tar.gz" if bb.utils.to_boolean(d.getVar('DIFF_UPDATE')) else "ubifs" }"
RAUC_SLOT_rootfs ?= "owa5-image-nand"
RAUC_SLOT_rootfs[fstype] := "${FSTYPE}"
RAUC_SLOT_rootfs[file] ?= "${RAUC_SLOT_rootfs}-${MACHINE}-rootfs.${FSTYPE}"

RAUC_KEY_FILE ?= "${CERT_KEY_NAME}"
RAUC_CERT_FILE ?= "${CERT_NAME}"
RAUC_KEYRING_FILE ?= "${TRUST_CHAIN}"

RAUC_CASYNC_BUNDLE = "${@ "1" if bb.utils.to_boolean(d.getVar('DIFF_UPDATE')) else "0" }"
OTA_TYPE = "${@ "delta" if bb.utils.to_boolean(d.getVar('DIFF_UPDATE')) else "full" }"

do_deploy() {
	install -d ${DEPLOYDIR}
	install -m 0644 ${B}/bundle.raucb ${DEPLOYDIR}/${BUNDLE_NAME}${BUNDLE_EXTENSION}
	ln -sf ${BUNDLE_NAME}${BUNDLE_EXTENSION} ${DEPLOYDIR}/${BUNDLE_LINK_NAME}${BUNDLE_EXTENSION}

	if [ ${RAUC_CASYNC_BUNDLE} -eq 1 ]; then
		install ${B}/casync-bundle.raucb ${DEPLOYDIR}/${CASYNC_BUNDLE_NAME}${CASYNC_BUNDLE_EXTENSION}
        tar -cvzf ${B}/casync-bundle.castr.tar.gz casync-bundle.castr --transform s/casync-bundle.castr/${CASYNC_BUNDLE_LINK_NAME}.castr/
		cp -r ${B}/casync-bundle.castr.tar.gz ${DEPLOYDIR}/${CASYNC_BUNDLE_NAME}.castr.tar.gz
		ln -sf ${CASYNC_BUNDLE_NAME}${CASYNC_BUNDLE_EXTENSION} ${DEPLOYDIR}/${CASYNC_BUNDLE_LINK_NAME}${CASYNC_BUNDLE_EXTENSION}
		ln -sf ${CASYNC_BUNDLE_NAME}.castr.tar.gz ${DEPLOYDIR}/${CASYNC_BUNDLE_LINK_NAME}.castr.tar.gz
	fi

    if [ ${UPLOAD_BUNDLE} -eq 1 ]; then
        ${WORKDIR}/upload-bundle -f ${DEPLOYDIR}/${BUNDLE_LINK_NAME}${BUNDLE_EXTENSION} -u ${ODM_USER} -p ${ODM_PASS} -o ${MACHINE} -c "${ODM_DESCRIPTION}" -t "${OTA_TYPE}"
    fi
}