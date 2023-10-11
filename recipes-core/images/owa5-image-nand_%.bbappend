IMAGE_INSTALL += " jq packagegroup-odm"
IMAGE_INSTALL:remove = "${@'first-boot-operations' if bb.utils.to_boolean(d.getVar('IS_BUNDLE')) else ''}"
IMAGE_INSTALL:append = "${@'fstab-bundle' if bb.utils.to_boolean(d.getVar('IS_BUNDLE')) else ''}"