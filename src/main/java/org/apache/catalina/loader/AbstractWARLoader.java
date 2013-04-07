package org.apache.catalina.loader;

import org.alauer.simplecontainer.WebappLoader;
import org.apache.catalina.LifecycleException;
import org.apache.naming.resources.WARDirContext;

import java.io.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public abstract class AbstractWARLoader implements WebappLoader {
    public static final String WEB_INF_CLASSES = "/WEB-INF/classes";
    public static final String WEB_INF_LIB = "/WEB-INF/lib";

    private WebappClassLoader webappClassLoader;
    private File explodedWarDir;
    private File warFile;

    public AbstractWARLoader(File explodedWarDir, File warFile) {
        this.webappClassLoader = new WebappClassLoader();
        this.explodedWarDir = explodedWarDir;
        this.warFile = warFile;
    }

    @Override
    public void init() {
        if (!explodedWarDir.exists() || !explodedWarDir.canWrite())
            throw new RuntimeException("Cannot write into exploded war directory at " + explodedWarDir.getAbsolutePath());

        if (!warFile.exists() || !warFile.canRead())
            throw new RuntimeException("Cannot read the WAR file at " + warFile.getAbsolutePath());

        try {
            unzip(warFile, explodedWarDir);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        WARDirContext warDirContext = new WARDirContext();
        warDirContext.setDocBase(warFile.getAbsolutePath());

        webappClassLoader.setResources(warDirContext);

        webappClassLoader.addRepository(WEB_INF_CLASSES + "/", new File(explodedWarDir.getAbsolutePath() + WEB_INF_CLASSES));

        webappClassLoader.addRepository("", new File(explodedWarDir.getAbsolutePath()));

        webappClassLoader.setJarPath(WEB_INF_LIB);

        try {
            for (File file : new File(explodedWarDir, WEB_INF_LIB).listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.getName().toLowerCase().endsWith(".jar");
                }
            }))
                webappClassLoader.addJar(WEB_INF_LIB + "/" + file.getName(), new JarFile(file), file);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void start() {
        try {
            webappClassLoader.start();
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ClassLoader getClassLoader() {
        return webappClassLoader;
    }

    protected File getExplodedWarDir() {
        return explodedWarDir;
    }

    protected File getWarFile() {
        return warFile;
    }

    protected void unzip(File zipFile, File outputFolder) throws IOException {
        byte[] buffer = new byte[1024 * 256];

        //get the zip file content
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile)); //get the zipped file list entry
        ZipEntry ze = zis.getNextEntry();

        while (ze != null) {
            String fileName = ze.getName();
            File newFile = new File(outputFolder + File.separator + fileName);

            if (ze.isDirectory()) {
                if (!newFile.exists() && !newFile.mkdirs())
                    throw new RuntimeException("Unable to create directory " + newFile.getAbsolutePath());
            } else {
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) fos.write(buffer, 0, len);

                fos.close();
            }

            ze = zis.getNextEntry();
        }

        zis.closeEntry();
        zis.close();
    }
}
