package com.carrotgarden.maven.scalor.util

import org.apache.maven.artifact.Artifact
import java.net.URLClassLoader
import java.net.URL

/**
 * Maven support.
 */
object Maven {

  /**
   * Maven identity based on G.A.V.
   */
  def artifactIdentity( artifact : Artifact, useVersion : Boolean = false ) = {
    import artifact._
    getGroupId + ":" + getArtifactId + ( if ( useVersion ) ":" + getVersion else "" )
  }

  /**
   * Compare artifacts by their identity.
   */
  def hasIdentityMatch( a1 : Artifact, a2 : Artifact, useVersion : Boolean = false ) = {
    if ( a1 == null || a2 == null ) {
      false
    } else {
      artifactIdentity( a1, useVersion ) == artifactIdentity( a2, useVersion )
    }
  }

  /**
   * Verify artifact has regex identity and a classifier.
   */
  def hasArtifactMatch( artifact : Artifact, regex : String, classifier : String ) : Boolean = {
    val hasIdentity = artifactIdentity( artifact ) match {
      case regex.r() => true
      case _         => false
    }
    val hasClassifier = artifact.getClassifier == classifier
    hasIdentity && hasClassifier
  }

  /**
   * Verify that artifact jar contains named resource.
   */
  def hasResourceMatch( artifact : Artifact, resource : String ) : Boolean = {
    import Folder._
    val url = ensureCanonicalFile( artifact.getFile ).toURI.toURL
    val loader = new URLClassLoader( Array[ URL ]( url ) );
    val result = loader.getResources( resource ).hasMoreElements
    loader.close
    result
  }

  /**
   * Find matching artifact in a list by identity regex and a classifier.
   */
  def extractArtifact(
    artifactList : Seq[ Artifact ], regex : String, classifier : String
  ) : Either[ Throwable, Artifact ] = {
    val collectList = artifactList.collect {
      case artifact if hasArtifactMatch( artifact, regex, classifier ) => artifact
    }
    collectList.toList match {
      case head :: Nil =>
        Right( head )
      case head :: tail =>
        Left( new RuntimeException( "Duplicate artifact: " + regex ) )
      case Nil =>
        Left( new RuntimeException( "Missing artifact: " + regex ) )
    }
  }

}