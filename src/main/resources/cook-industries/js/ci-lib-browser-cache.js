/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 *
 * author: <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */

import { Logger } from '/js/ci-lib-logger.js';

export { BrowserCache };

const KEY_PREFIX = "ci-lib";
const LOGGER = Logger.createFor("ci-lib-browser-cache");

/**
 * The browser storage shared by all open tabs and windows of this origin, or null if it is blocked.
 */
function resolveStorage() {
  try {
    const storage = window.localStorage;
    const probe = `${KEY_PREFIX}:probe`;

    storage.setItem(probe, "1");
    storage.removeItem(probe);

    return storage;
  } catch (error) {
    LOGGER.warn("browser storage is not available, values are only kept until the page is closed.", { error });

    return null;
  }
}

/**
 * A key/value cache for JSON serializable values inside one namespace of the browser storage.
 *
 * Values are shared with every other tab of the same origin. Without an usable browser storage the values live in memory of this page
 * only, the API stays the same.
 */
class BrowserCache {
  #prefix;
  #storage;
  #memory = new Map();

  /**
   * @param {string} namespace - separates this cache from all others, e.g. "translations"
   * @returns {BrowserCache}
   */
  static create(namespace) {
    return new BrowserCache(namespace);
  }

  /**
   * @param {string} namespace
   */
  constructor(namespace) {
    if (typeof namespace !== "string" || namespace === "") {
      throw new Error("a cache namespace is required");
    }

    this.#prefix = `${KEY_PREFIX}:${namespace}:`;
    this.#storage = resolveStorage();
  }

  /**
   * @param {string} key
   * @param {*} [fallback] - returned if there is no value for the key or it cannot be read
   * @returns {*} the stored value
   */
  get(key, fallback = null) {
    const raw = this.#read(this.#prefix + key);

    if (raw === null) {
      return fallback;
    }

    try {
      return JSON.parse(raw);
    } catch (error) {
      LOGGER.warn("cached value is not readable, dropping it.", { key, error });

      this.remove(key);

      return fallback;
    }
  }

  /**
   * @param {string} key
   * @param {*} value - anything JSON serializable
   * @returns {boolean} false if the value could only be kept in memory of this page
   */
  set(key, value) {
    const storageKey = this.#prefix + key;
    const raw = JSON.stringify(value);

    this.#memory.set(storageKey, raw);

    if (this.#storage === null) {
      return false;
    }

    try {
      this.#storage.setItem(storageKey, raw);

      return true;
    } catch (error) {
      LOGGER.warn("value could not be written to the browser storage, keeping it in memory.", { key, error });

      return false;
    }
  }

  /**
   * @param {string} key
   */
  remove(key) {
    const storageKey = this.#prefix + key;

    this.#memory.delete(storageKey);

    try {
      this.#storage?.removeItem(storageKey);
    } catch (error) {
      LOGGER.warn("value could not be removed from the browser storage.", { key, error });
    }
  }

  /**
   * Removes all values of this namespace.
   */
  clear() {
    this.keys().forEach((key) => this.remove(key));
  }

  /**
   * @returns {string[]} all keys of this namespace
   */
  keys() {
    const storageKeys = new Set(this.#memory.keys());

    try {
      for (let i = 0; this.#storage !== null && i < this.#storage.length; i++) {
        storageKeys.add(this.#storage.key(i));
      }
    } catch (error) {
      LOGGER.warn("keys could not be read from the browser storage.", { error });
    }

    return [...storageKeys].filter((storageKey) => storageKey.startsWith(this.#prefix)).map((storageKey) => storageKey.substring(this.#prefix.length));
  }

  /**
   * Calls back when another tab of this origin changes or removes the value. Changes made by this page are not reported.
   *
   * @param {string} key
   * @param {function(*): void} callback - receives the new value, null if it was removed
   * @returns {function(): void} stops the listening
   */
  onChange(key, callback) {
    const storageKey = this.#prefix + key;

    const listener = (event) => {
      if (event.storageArea !== this.#storage || (event.key !== storageKey && event.key !== null)) {
        return;
      }

      if (event.newValue === null) {
        this.#memory.delete(storageKey);

        callback(null);

        return;
      }

      this.#memory.set(storageKey, event.newValue);

      callback(this.get(key));
    };

    window.addEventListener("storage", listener);

    return () => window.removeEventListener("storage", listener);
  }

  #read(storageKey) {
    try {
      const raw = this.#storage?.getItem(storageKey) ?? null;

      if (raw !== null) {
        return raw;
      }
    } catch (error) {
      LOGGER.warn("value could not be read from the browser storage.", { error });
    }

    return this.#memory.get(storageKey) ?? null;
  }
}
