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
package ru.beykerykt.minecraft.lightapi.bukkit.internal.handler.craftbukkit;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;

import ru.beykerykt.minecraft.lightapi.bukkit.internal.BukkitPlatformImpl;
import ru.beykerykt.minecraft.lightapi.bukkit.internal.handler.IHandler;
import ru.beykerykt.minecraft.lightapi.bukkit.internal.handler.IHandlerFactory;

public class HandlerFactory implements IHandlerFactory {

    private static final String CRAFTBUKKIT_PKG = "org.bukkit.craftbukkit";
    private static final String[] STARLIGHT_ENGINE_PKG = {
            "ca.spottedleaf.starlight.light.StarLightEngine",
            "ca.spottedleaf.starlight.common.light.StarLightEngine"
    };
    private BukkitPlatformImpl mPlatformImpl;

    private BukkitPlatformImpl getPlatformImpl() {
        return mPlatformImpl;
    }

    private boolean isStarlight() {
        for (String pkg : STARLIGHT_ENGINE_PKG) {
            try {
                Class.forName(pkg);
                return true;
            } catch (ClassNotFoundException e) {
                getPlatformImpl().debug("Class " + pkg + " not found");
            }
        }
        return false;
    }

    @Override
    public IHandler createHandler(BukkitPlatformImpl impl) throws Exception {
        this.mPlatformImpl = impl;
        IHandler handler = null;
        String serverImplPackage = Bukkit.getServer().getClass().getPackage().getName();

        if (serverImplPackage.startsWith(CRAFTBUKKIT_PKG)) { // make sure it's craftbukkit
            String[] line = serverImplPackage.replace(".", ",").split(",");
            String version = line[3];

            String handlerClassName = (isStarlight() ? "Starlight" : "Vanilla") + "NMSHandler";
            String handlerPath = getClass().getPackage().getName() + ".nms." + version + "." + handlerClassName;
            // start using nms handler
            handler = (IHandler) Class.forName(handlerPath).getConstructor().newInstance();
        } else { // something else
            throw new NotImplementedException(Bukkit.getName() + " is currently not supported.");
        }
        return handler;
    }
}
