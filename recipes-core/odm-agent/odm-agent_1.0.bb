SUMMARY = "Owasys Device Manager Agent"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM ="file://${COMPANY_CUSTOM_LICENSES}/OWASYS_Propietary_SW_License_Agreement.md;md5=203a753c44e11367199c31c2168fa959"

inherit systemd

DEPENDS += " dbus owasys-libpollux hiredis libcrypto-1-1 libev libmnl paho-mqtt-c "
RDEPENDS:${PN} += " owasys-libpollux "

SRC_URI = " file://odm-agent \
            file://odm.json \
            file://odm-attributes.sh \
            file://odm-commands.sh \
            file://libodm-hw.so \
            file://libodm-sys.so \
            file://libodm-net.so \
            file://libodm-pos.so \
            file://owasysd-odm-agent.service \
            file://owasys_chain.pem \
"

INSANE_SKIP:${PN} += "already-stripped dev-so"
SOLIBS = ".so"
FILES_SOLIBSDEV = ""
SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE:${PN} ="owasysd-odm-agent.service"

do_install() {

  install -d ${D}${bindir}
  install -d ${D}${libdir}/owasys
  install -d ${D}${sysconfdir}/odm
  install -d ${D}${sysconfdir}/systemd/system
  install -d ${D}${sysconfdir}/ssl/certs

  install -m 0755 ${WORKDIR}/odm-agent         ${D}${bindir}
  install -m 0755 ${WORKDIR}/odm-attributes.sh ${D}${bindir}
  install -m 0755 ${WORKDIR}/odm-commands.sh   ${D}${bindir}

  install ${WORKDIR}/libodm-hw.so  ${D}${libdir}/owasys
  install ${WORKDIR}/libodm-sys.so ${D}${libdir}/owasys
  install ${WORKDIR}/libodm-net.so ${D}${libdir}/owasys
  install ${WORKDIR}/libodm-pos.so ${D}${libdir}/owasys

  install -d ${D}/${nonarch_base_libdir}
  ln -s -r ${D}/${libdir}/owasys/libodm-hw.so     ${D}${nonarch_base_libdir}/libodm-hw.so
  ln -s -r ${D}/${libdir}/owasys/libodm-sys.so    ${D}${nonarch_base_libdir}/libodm-sys.so
  ln -s -r ${D}/${libdir}/owasys/libodm-net.so    ${D}${nonarch_base_libdir}/libodm-net.so
  ln -s -r ${D}/${libdir}/owasys/libodm-pos.so    ${D}${nonarch_base_libdir}/libodm-pos.so

  install ${WORKDIR}/odm.json                     ${D}${sysconfdir}/odm
  install ${WORKDIR}/owasys_chain.pem             ${D}${sysconfdir}/ssl/certs
  install -m 0644 ${WORKDIR}/owasysd-odm-agent.service    ${D}${sysconfdir}/systemd/system
}

FILES:${PN} += " \
    ${bindir}/* \
    ${sysconfdir}/odm/odm.json \
    ${sysconfdir}/systemd/system/owasysd-odm-agent.service \
    ${libdir}/* \
    ${nonarch_base_libdir}/* \
"