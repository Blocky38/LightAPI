/*
 * The MIT License (MIT)
 *
 * Copyright 2021 Vladimir Mikhailov <beykerykt@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package ru.beykerykt.minecraft.lightapi.common.internal.storage;

import java.util.HashMap;
import java.util.Map;

import ru.beykerykt.minecraft.lightapi.common.api.ResultCode;
import ru.beykerykt.minecraft.lightapi.common.internal.utils.BlockPosition;

public class LightStorage {

    private String mWorldName = null;
    private Map<Long, Integer> mLightLevels = new HashMap();
    private IStorageProvider mStorageProvider = null;

    public LightStorage(String worldName, IStorageProvider provider) {
        this.mWorldName = worldName;
        this.mStorageProvider = provider != null ? provider : new EmptyStorageProvider();
    }

    public void destroy() {
        mWorldName = null;
        mLightLevels.clear();
        mLightLevels = null;
        mStorageProvider = null;
    }

    /**
     * N/A
     */
    public String getWorldName() {
        return mWorldName;
    }

    /*
     * Light Level
     */

    /**
     * N/A
     */
    public void setLightLevel(BlockPosition pos, int lightLevel) {
        long longPos = pos.asLong();
        setLightLevel(longPos, lightLevel);
    }

    /**
     * N/A
     */
    public void setLightLevel(long longPos, int lightLevel) {
        if (lightLevel > 0) {
            if (! mLightLevels.containsKey(longPos)) {
                mLightLevels.put(longPos, lightLevel);
            }
        } else {
            mLightLevels.remove(longPos);
        }
    }

    /**
     * N/A
     */
    public boolean checkLightLevel(BlockPosition pos) {
        long longPos = pos.asLong();
        return checkLightLevel(longPos);
    }

    public boolean checkLightLevel(long longPos) {
        return mLightLevels.containsKey(longPos);
    }

    /**
     * N/A
     */
    public int getLightLevel(long longPos) {
        if (! checkLightLevel(longPos)) {
            return - 1;
        }
        return mLightLevels.get(longPos);
    }

    /*
     * Storage Provider
     */

    /**
     * N/A
     */
    public int restore() {
        if (mWorldName == null) {
            return ResultCode.FAILED;
        }
        if (mStorageProvider == null) {
            return ResultCode.FAILED;
        }
        mLightLevels.clear();
        mLightLevels.putAll(mStorageProvider.loadLightLevels(getWorldName()));
        return ResultCode.SUCCESS;
    }

    /**
     * N/A
     */
    public int save() {
        if (mWorldName == null) {
            return ResultCode.FAILED;
        }
        if (mStorageProvider == null) {
            return ResultCode.FAILED;
        }

        int code = mStorageProvider.saveLightLevels(getWorldName(), mLightLevels);
        // something is wrong ?
        return code;
    }
}
