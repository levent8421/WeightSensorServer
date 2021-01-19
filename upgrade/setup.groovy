TARGET_PATH = '/opt/weight_slot'

def cleanup() {
    try {
        tools.delete(TARGET_PATH, true)
    } catch (IOException e) {
        log.warn("Cannot delete ${TARGET_PATH}", e)
    }
    tools.mkdirs(TARGET_PATH)
}

def moveAppFile() {
    tools.mv("${workspace}/digital_weight_sensor.jar", "${TARGET_PATH}/digital_weight_sensor.jar")
}

def moveScript() {
    scripts = [
            'mysql-shell.sh',
            'deploy.sh',
            'remote-debug.sh',
            'shutdown.sh',
            'startup.sh',
            'tail-log.sh',
    ]
    scripts.each {
        target = "${TARGET_PATH}/${it}"
        source = "${workspace}/${it}"
        tools.mv(source, target)
        tools.chmod(target, true, true, true)
    }
}

def moveFile() {
    log.debug('Starting......')
    tools.mkdirs("${TARGET_PATH}/log")
    log.debug('Init dir success!')
    moveAppFile()
    moveScript()
    log.debug('Move files success!')
}

def restart() {
    log.debug('Starting app......')
    tools.exec("chmod +x ${TARGET_PATH}/*.sh & ${TARGET_PATH}/startup.sh -s > /${TARGET_PATH}/auto_startup.log")
}

cleanup()
moveFile()
restart()
TARGET_PATH