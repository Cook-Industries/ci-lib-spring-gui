export {
    Logger
}

const LEVEL = {
    TRACE: {
        value: 0,
        color: "#888888"
    },

    DEBUG: {
        value: 1,
        color: "#3498db"
    },

    INFO: {
        value: 2,
        color: "#2ecc71"
    },

    WARN: {
        value: 3,
        color: "#f39c12"
    },

    ERROR: {
        value: 4,
        color: "#e74c3c"
    },

    NONE: {
        value: 5,
        color: "#ffffff"
    }
};

const LEVEL_WIDTH = 5;
const MODULE_WIDTH = 20;

function formatLevel(levelName) {
    if (!levelName) return " ".repeat(LEVEL_WIDTH);

    return levelName.length > LEVEL_WIDTH
        ? levelName.slice(0, LEVEL_WIDTH)
        : levelName.padEnd(LEVEL_WIDTH, " ");
}

function formatModule(module) {
    if (!module) return " ".repeat(MODULE_WIDTH);

    return module.length > MODULE_WIDTH
        ? module.slice(0, MODULE_WIDTH - 1) + "…"
        : module.padEnd(MODULE_WIDTH, " ");
}

const Logger = (() => {
    let currentLevel = LEVEL.INFO;

    function setLevel(level) {
        currentLevel = level;
    }

    function createFor(module) {
        if (!module || typeof module !== "string") {
            throw new Error("Logger requires a module name (string).");
        }

        function log(levelName, level, ...args) {
            if (currentLevel > level.value) return;

            const time = new Date().toISOString();
            const lvl = formatLevel(levelName);
            const mod = formatModule(module);

            console.log(
                `%c[${time}]%c[${lvl}]%c[${mod}]%c`,
                "color: white",
                `color: ${level.color}`,
                "color: cyan",
                "color: white",
                ...args
            );
        }

        return {
            trace: (...args) => log("TRACE", LEVEL.TRACE, ...args),
            debug: (...args) => log("DEBUG", LEVEL.DEBUG, ...args),
            info: (...args) => log("INFO ", LEVEL.INFO, ...args),
            warn: (...args) => log("WARN ", LEVEL.WARN, ...args),
            error: (...args) => log("ERROR", LEVEL.ERROR, ...args),
        };
    }

    return {
        setLevel,
        createFor
    };
})();