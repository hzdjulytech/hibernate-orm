/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.boot.jaxb.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.xml.transform.dom.DOMSource;

import org.hibernate.boot.MappingNotFoundException;
import org.hibernate.boot.archive.spi.InputStreamAccess;
import org.hibernate.boot.jaxb.JaxbLogger;
import org.hibernate.boot.jaxb.Origin;
import org.hibernate.boot.jaxb.SourceType;
import org.hibernate.boot.jaxb.spi.XmlSource;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;

import org.w3c.dom.Document;

/**
 * Helper for building and handling {@link XmlSource} references
 *
 * @author Steve Ebersole
 */
public class XmlSources {
	/**
	 * Create an {@link XmlSource} from a named resource
	 */
	public static XmlSource fromResource(String resourceName, ClassLoaderService classLoaderService) {
		JaxbLogger.JAXB_LOGGER.tracef( "reading mappings from resource : %s", resourceName );

		final Origin origin = new Origin( SourceType.RESOURCE, resourceName );
		final URL url = classLoaderService.locateResource( resourceName );
		if ( url == null ) {
			throw new MappingNotFoundException( origin );
		}

		return new UrlXmlSource( origin, url );
	}

	/**
	 * Create an {@link XmlSource} from a URL
	 */
	public static XmlSource fromUrl(URL url) {
		final String urlExternalForm = url.toExternalForm();
		JaxbLogger.JAXB_LOGGER.tracef( "Reading mapping document from URL : %s", urlExternalForm );

		final Origin origin = new Origin( SourceType.URL, urlExternalForm );
		return new UrlXmlSource( origin, url );
	}

	public static XmlSource fromFile(File file) {
		final String filePath = file.getPath();
		JaxbLogger.JAXB_LOGGER.tracef( "reading mappings from file : %s", filePath );

		final Origin origin = new Origin( SourceType.FILE, filePath );

		if ( !file.exists() ) {
			throw new MappingNotFoundException( origin );
		}

		return new FileXmlSource( origin, file );
	}

	public static XmlSource fromCacheableFile(File file) {
		return fromCacheableFile( file, file.getParentFile() );
	}

	public static XmlSource fromCacheableFile(File file, File cacheableDir) {
		return fromCacheableFile( file, cacheableDir, false );
	}

	public static XmlSource fromCacheableFile(File file, boolean strict) {
		return fromCacheableFile( file, file.getParentFile(), strict );
	}

	public static XmlSource fromCacheableFile(File file, File cacheableDir, boolean strict) {
		final String filePath = file.getPath();
		JaxbLogger.JAXB_LOGGER.tracef( "reading mappings from cacheable-file : %s", filePath );

		final Origin origin = new Origin( SourceType.FILE, filePath );
		return new CacheableFileXmlSource( origin, file, cacheableDir, strict );
	}

	public static XmlSource fromStream(InputStreamAccess inputStreamAccess) {
		final String streamName = inputStreamAccess.getStreamName();
		JaxbLogger.JAXB_LOGGER.tracef( "reading mappings from InputStreamAccess : %s", streamName );

		final Origin origin = new Origin( SourceType.INPUT_STREAM, streamName );
		return new InputStreamAccessXmlSource( origin, inputStreamAccess );
	}

	public static XmlSource fromStream(InputStream inputStream) {
		JaxbLogger.JAXB_LOGGER.trace( "reading mappings from InputStream" );

		final Origin origin = new Origin( SourceType.INPUT_STREAM, null );
		return new InputStreamXmlSource( origin, inputStream, false );
	}

	public static XmlSource fromDocument(Document document) {
		JaxbLogger.JAXB_LOGGER.trace( "reading mappings from DOM" );
		final Origin origin = new Origin( SourceType.DOM, Origin.UNKNOWN_FILE_PATH );
		return new JaxpSourceXmlSource( origin, new DOMSource( document ) );
	}

	public static void fromJar(File jar, Consumer<XmlSource> consumer) {
		JaxbLogger.JAXB_LOGGER.tracef( "Seeking mapping documents in jar file : %s", jar.getName() );

		final Origin origin = new Origin( SourceType.JAR, jar.getAbsolutePath() );

		try {
			final JarFile jarFile = new JarFile( jar );

			try {
				final Enumeration<JarEntry> entries = jarFile.entries();
				while ( entries.hasMoreElements() ) {
					final JarEntry jarEntry = entries.nextElement();
					if ( jarEntry.getName().endsWith( ".hbm.xml" ) ) {
						JaxbLogger.JAXB_LOGGER.tracef( "Found hbm.xml mapping in jar : %s", jarEntry.getName() );
						consumer.accept( new JarFileEntryXmlSource( origin, jarFile, jarEntry ) );
					}
				}
			}
			finally {
				try {
					jarFile.close();
				}
				catch ( Exception ignore ) {
				}
			}
		}
		catch ( IOException e ) {
			throw new MappingNotFoundException( e, origin );
		}
	}
}
