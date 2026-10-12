/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 *
 * author: <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */

import { BrowserCache } from '/js/ci-lib-browser-cache.js';
import { Logger } from '/js/ci-lib-logger.js';

export { Translations };

const LOGGER = Logger.createFor("ci-lib-translations");

const CACHE = BrowserCache.create("translations");

const PLACEHOLDER = /\{(\d+)\}/g;

let config = null;
let ready = Promise.resolve();

function isBundle(value) {
  return value !== null && typeof value === "object" && typeof value.checksum === "string" && value.texts !== null && typeof value.texts === "object";
}

/**
 * A set of texts that is kept in the browser storage and validated against the server by checksum.
 *
 * @param {string} path - path of the endpoint below the base url, the checksum is expected at <path>/checksum
 * @param {function(): object} [extraHeaders] - headers added to every request of this source
 */
function createSource(path, extraHeaders = () => ({})) {
  let bundle = { checksum: null, texts: {} };
  let cacheKey = null;
  let stopListening = () => {};

  async function fetchChecksum() {
    const response = await config.request(`${config.url}${path}/checksum`, {
      method: "GET",
      headers: { Accept: "text/plain, application/json", ...extraHeaders() },
    });

    if (!response.ok) {
      throw new Error(`checksum request for [${path}] failed with status [${response.status}]`);
    }

    return (await response.text()).trim().replace(/^"|"$/g, "");
  }

  async function fetchBundle() {
    const response = await config.request(`${config.url}${path}`, { method: "GET", headers: { Accept: "application/json", ...extraHeaders() } });

    if (!response.ok) {
      throw new Error(`translation request for [${path}] failed with status [${response.status}]`);
    }

    const fetched = await response.json();

    if (!isBundle(fetched)) {
      throw new Error(`translation response for [${path}] has no checksum and texts`);
    }

    return fetched;
  }

  return {
    /**
     * Switches to the texts stored under a key of the browser storage, an unknown key starts empty.
     *
     * @param {string} key
     */
    use(key) {
      stopListening();

      cacheKey = key;

      const cached = CACHE.get(key);

      bundle = isBundle(cached) ? cached : { checksum: null, texts: {} };

      stopListening = CACHE.onChange(key, (changed) => {
        if (isBundle(changed)) {
          bundle = changed;
        }
      });
    },

    has(key) {
      return Object.hasOwn(bundle.texts, key);
    },

    get(key) {
      return bundle.texts[key];
    },

    /**
     * Asks the server for the current checksum and fetches the texts only if it differs from the cached one.
     *
     * @returns {Promise<void>} resolves in any case, a failure keeps the texts that are already known
     */
    async refresh() {
      const key = cacheKey;

      try {
        const checksum = await fetchChecksum();

        if (checksum === bundle.checksum) {
          LOGGER.debug("translations are up to date.", { path, checksum });

          return;
        }

        LOGGER.debug("translations are stale, fetching.", { path, cached: bundle.checksum, current: checksum });

        bundle = await fetchBundle();

        CACHE.set(key, bundle);
      } catch (error) {
        LOGGER.warn("translations could not be refreshed, keeping the known ones.", { path, error });
      }
    },
  };
}

const translationSource = createSource("", () => (config.locale ? { "Accept-Language": config.locale } : {}));
const fallbackSource = createSource("/fallback");

fallbackSource.use("fallback");

function start() {
  ready = Promise.all([fallbackSource.refresh(), translationSource.refresh()]).then(() => undefined);

  return ready;
}

/**
 * Translations for the browser side of the library, kept in the browser storage and validated against the server by checksum.
 */
const Translations = {
  /**
   * Starts the check of the translations and of the fallback texts against the server. The cached translations are usable right away.
   *
   * @param {object} options
   * @param {string} options.url - base url of the translation endpoint
   * @param {function(string, object): Promise<Response>} [options.request] - fetch compatible function, defaults to fetch
   * @param {string} [options.locale] - language tag sent as Accept-Language, the browser setting is used if omitted
   * @returns {Promise<void>} resolves once the check is done
   */
  init({ url, request = (target, settings) => fetch(target, settings), locale = "" }) {
    if (typeof url !== "string" || url === "") {
      throw new Error("a translation url is required");
    }

    config = { url: url.replace(/\/+$/, ""), request, locale };

    translationSource.use(locale ? `bundle:${locale}` : "bundle");

    return start();
  },

  /**
   * Runs the check against the server again.
   *
   * @returns {Promise<void>}
   */
  refresh() {
    return start();
  },

  /**
   * @returns {Promise<void>} the loading started last
   */
  get ready() {
    return ready;
  },

  /**
   * @param {string} key
   * @returns {boolean} whether a translation or a fallback text for the key is known
   */
  has(key) {
    return translationSource.has(key) || fallbackSource.has(key);
  },

  /**
   * @param {string} key
   * @param {string} [fallback] - returned if neither a translation nor a server fallback is known, a marker text if omitted
   * @param {...*} args - fill the placeholders {0}, {1}, ... of the translation
   * @returns {string} the translation
   */
  translate(key, fallback = undefined, ...args) {
    if (!Translations.has(key)) {
      LOGGER.warn("no translation defined.", { key });

      return fallback === undefined ? `I18N [${key}] not set.` : fallback;
    }

    const text = translationSource.has(key) ? translationSource.get(key) : fallbackSource.get(key);

    return text.replace(PLACEHOLDER, (placeholder, index) => (args[index] === undefined ? placeholder : String(args[index])));
  },
};
