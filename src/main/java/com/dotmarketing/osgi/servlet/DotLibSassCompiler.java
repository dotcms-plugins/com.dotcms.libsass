package com.dotmarketing.osgi.servlet;

import com.dotcms.contenttype.model.type.BaseContentType;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Identifier;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.DotStateException;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.contentlet.model.ContentletVersionInfo;
import com.dotmarketing.portlets.fileassets.business.FileAsset;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.TrashUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import com.google.common.io.Files;
import com.liferay.util.FileUtil;

import io.bit3.jsass.Options;
import io.bit3.jsass.Output;

public class DotLibSassCompiler extends com.dotcms.enterprise.csspreproc.CSSCompiler {

    boolean live=true;
    public DotLibSassCompiler(Host host, String uri, boolean live) {
        super(host, uri, live);
        this.live=live;
    }

    @Override
    public void compile() throws DotSecurityException, DotStateException, DotDataException, IOException {
        
        final File compileDir = Files.createTempDir();
        try {
            // replace the extension .css with .scss
            String trying = inputURI.substring(0, inputURI.lastIndexOf('.')) + ".scss";
            
            Identifier ident = APILocator.getIdentifierAPI().find(inputHost, trying);
            if(ident==null || null ==ident.getId()) {
                trying = inputURI.substring(0, inputURI.lastIndexOf('.')) + ".sass";
                ident = APILocator.getIdentifierAPI().find(inputHost, trying);
                if(ident==null || null ==ident.getId()) {
                    throw new IOException("file: "+inputHost.getHostname()+":"+ trying +" not found");
                }
            }
            
            ContentletVersionInfo info = APILocator.getVersionableAPI().getContentletVersionInfo(ident.getId(), APILocator.getLanguageAPI().getDefaultLanguage().getId());
            FileAsset mainFile = APILocator.getFileAssetAPI().fromContentlet(APILocator.getContentletAPI().find(info.getWorkingInode(), APILocator.systemUser(), true));
            

            String query = "+path:" + ident.getParentPath() + "* +baseType:" +BaseContentType.FILEASSET.ordinal() + " +conHost:" + ident.getHostId() + " +live:" + this.live;
            List<Contentlet> files = APILocator.getContentletAPI().search(query, 1000, 0, null, APILocator.systemUser(), true);
            for(Contentlet con : files) {
                FileAsset asset = APILocator.getFileAssetAPI().fromContentlet(con);
                File f = new File(compileDir.getAbsolutePath() + asset.getPath() + File.separator + asset.getFileName());
                f.getParentFile().mkdirs();
                FileUtil.copyFile(asset.getFileAsset(), f);
            }
            

            // build directories to build scss
            File compileTargetFile = new File( compileDir.getAbsolutePath() + mainFile.getPath() + File.separator + mainFile.getFileName() );
            File compileDestinationFile = new File( compileTargetFile.getAbsoluteFile() + ".css" );
            URI inputFile = compileTargetFile.toURI();
            URI outputFile = compileDestinationFile.toURI();

            io.bit3.jsass.Compiler compiler = new io.bit3.jsass.Compiler();
            Options options = new Options();
            Output out = compiler.compileFile(inputFile, outputFile, options);

            this.output=out.getCss().getBytes();

            
        }
        catch(Exception ex) {
            Logger.error(this, "unable to compile sass code "+inputHost.getHostname()+":"+inputURI+" live:"+inputLive,ex);
            throw new RuntimeException(ex);
        }
        finally {
            FileUtil.deltree(compileDir);
            //new TrashUtils().moveFileToTrash(compileDir, "css");
        }
        
    }

    @Override
    protected String addExtensionIfNeeded(String url) {
        return url.indexOf('.',url.lastIndexOf('/'))!=-1 ? url : 
            url.substring(0, url.lastIndexOf('/'))+ "/_" + url.substring(url.lastIndexOf('/')+1, url.length()) +".scss";
    }

    @Override
    public String getDefaultExtension() {
        return "scss";
    }

}
