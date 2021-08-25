package com.apollographql.apollo3.compiler

import com.apollographql.apollo3.compiler.ApolloMetadata.Companion.merge
import com.apollographql.apollo3.ast.SourceAwareException
import com.apollographql.apollo3.compiler.Options.Companion.defaultCustomScalarsMapping
import com.google.common.truth.Truth
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.io.File

class MetadataTest {
  private val buildDir = File("build/metadata-test/")
  private val rootSchemaFile = File(buildDir, "root/graphql/schema.sdl")
  private val rootSourcesDir = File(buildDir, "root/sources")
  private val rootMetadataFile = File(buildDir, "root/metadata/metadata.json")
  private val leafGraphqlDir = File(buildDir, "leaf/graphql")
  private val leafSourcesDir = File(buildDir, "leaf/sources")
  private val leafMetadataFile = File(buildDir, "leaf/metadata/metadata.json")

  @Before
  fun before() {
    buildDir.deleteRecursively()
    buildDir.mkdirs()
  }

  private fun compile(
      operationFiles: Set<File>,
      outputDir: File,
      schemaFile: File?,
      metadataFiles: List<File> = emptyList(),
      alwaysGenerateTypesMatching: Set<String> = emptySet(),
      metadataOutputFile: File,
  ) {
    val incomingOptions = if (schemaFile != null) {
      IncomingOptions.fromOptions(
          schemaFiles = setOf(schemaFile),
          customScalarsMapping = defaultCustomScalarsMapping,
          codegenModels = MODELS_RESPONSE_BASED,
          packageNameGenerator = PackageNameGenerator.Flat(""),
          flattenModels = false
      )
    } else {
      val metadata = metadataFiles.map { ApolloMetadata.readFrom(it) }.merge()
      check(metadata != null)
      IncomingOptions.fromMetadata(metadata = metadata)
    }

    GraphQLCompiler.write(
        Options(
            executableFiles = operationFiles,
            outputDir = outputDir,
            alwaysGenerateTypesMatching = alwaysGenerateTypesMatching,
            metadataOutputFile = metadataOutputFile,
            packageNameGenerator = PackageNameGenerator.Flat(""),
            incomingOptions = incomingOptions,
        )
    )
  }

  private fun alwaysGenerateTypesMatchingTest(alwaysGenerateTypesMatching: Set<String>) {
    val schema = SchemaGenerator.generateSDLSchemaWithInputTypes(6)

    rootSchemaFile.parentFile.mkdirs()
    rootSchemaFile.writeText(schema)

    compile(
        operationFiles = emptySet(),
        schemaFile = rootSchemaFile,
        alwaysGenerateTypesMatching = alwaysGenerateTypesMatching,
        outputDir = rootSourcesDir,
        metadataOutputFile = rootMetadataFile
    )


    val leafFolders = listOf(leafGraphqlDir)
    leafGraphqlDir.mkdirs()
    File(leafGraphqlDir, "queries.graphql").writeText(SchemaGenerator.generateMutation())
    compile(
        operationFiles = leafFolders.graphqlFiles(),
        schemaFile = null,
        metadataFiles = listOf(rootMetadataFile),
        outputDir = leafSourcesDir,
        metadataOutputFile = leafMetadataFile,
    )

    KotlinCompiler.assertCompiles(listOf(rootSourcesDir, leafSourcesDir).kotlinFiles(), true)
  }

  @Test
  fun `empty alwaysGenerateTypesMatching does not generate types in root`() {
    alwaysGenerateTypesMatchingTest(emptySet())

    // Only scalar types are generated in the root
    rootSourcesDir.assertContents("Types.kt")

    // Leaf contains its referenced types but not the unused ones
    leafSourcesDir.assertContents(
        "Body0.kt",
        "Body0_InputAdapter.kt",
        "Encoding.kt",
        "Encoding_ResponseAdapter.kt",
        "MessageInput0.kt",
        "MessageInput0_InputAdapter.kt",
        "SendMessageMutation.kt",
        "SendMessageMutation_ResponseAdapter.kt",
        "SendMessageMutationSelections.kt",
        "SendMessageMutation_VariablesAdapter.kt",
        "User0.kt",
        "User0_InputAdapter.kt"
    )
  }

  @Test
  fun `alwaysGenerateTypesMatching can force generating a type not used downstream`() {
    alwaysGenerateTypesMatchingTest(setOf(".*1"))

    // types ending with "1" end up in root
    // Encoding is present as well because it is referenced from MessageInput
    rootSourcesDir.assertContents(
        "Body1.kt",
        "Body1_InputAdapter.kt",
        "Types.kt",
        "Encoding.kt",
        "Encoding_ResponseAdapter.kt",
        "MessageInput1.kt",
        "MessageInput1_InputAdapter.kt",
        "User1.kt",
        "User1_InputAdapter.kt"
    )

    // Leaf contains .*0 but not .*1
    // Encoding shouldn't be written as it was already generated
    leafSourcesDir.assertContents(
        "Body0.kt",
        "Body0_InputAdapter.kt",
        "MessageInput0.kt",
        "MessageInput0_InputAdapter.kt",
        "SendMessageMutation.kt",
        "SendMessageMutation_ResponseAdapter.kt",
        "SendMessageMutationSelections.kt",
        "SendMessageMutation_VariablesAdapter.kt",
        "User0.kt",
        "User0_InputAdapter.kt"
    )
  }

  private fun fragmentTest(dirName: String) {
    val folder = File("src/test/metadata/$dirName/")
    compile(
        operationFiles = setOf(File(folder, "root.graphql")),
        schemaFile = File("src/test/metadata/schema.sdl"),
        alwaysGenerateTypesMatching = emptySet(),
        outputDir = rootSourcesDir,
        metadataOutputFile = rootMetadataFile,
    )

    compile(
        operationFiles = setOf(File(folder, "leaf.graphql")),
        schemaFile = null,
        metadataFiles = listOf(rootMetadataFile),
        outputDir = leafSourcesDir,
        metadataOutputFile = leafMetadataFile,
    )

    KotlinCompiler.assertCompiles(listOf(rootSourcesDir, leafSourcesDir).kotlinFiles(), false)
  }

  @Test
  fun `fragments can be reused`() {
    fragmentTest("simple")

    // Root generates the fragment
    rootSourcesDir.assertContents(
        "CharacterFragment.kt",
        "CharacterFragmentSelections.kt",
        "Types.kt",
        "Episode.kt",
        "Episode_ResponseAdapter.kt"
    )

    // Leaf contains the query but not the fragment
    leafSourcesDir.assertContents(
        "GetHeroQuery.kt",
        "GetHeroQuery_ResponseAdapter.kt",
        "GetHeroQuerySelections.kt"
    )
  }

  @Test
  fun `fragments validation error`() {
    try {
      fragmentTest("fragment-variable-error")
      fail("Parsing the fragment should have failed")
    } catch (e: SourceAwareException) {
      Truth.assertThat(e.message).contains("Variable `first` is not defined by operation `GetHero`")
    }

    // Nothing is generated
    leafSourcesDir.assertContents()
  }

  @Test
  fun `fragments nameclash error`() {
    try {
      fragmentTest("fragment-nameclash-error")
      fail("Parsing the fragment should have failed")
    } catch (e: SourceAwareException) {
      Truth.assertThat(e.message).contains("Fragment CharacterFragment is already defined")
    }

    // Nothing is generated
    leafSourcesDir.assertContents()
  }

  @Test
  fun `fragments in both parent and child`() {
    fragmentTest("fragment-multiple")

    rootSourcesDir.assertContents(
        "CharacterFragment.kt",
        "CharacterFragmentSelections.kt",
        "Types.kt"
    )

    leafSourcesDir.assertContents(
        "Episode.kt",
        "Episode_ResponseAdapter.kt",
        "GetHeroQuery.kt",
        "GetHeroQuery_ResponseAdapter.kt",
        "GetHeroQuerySelections.kt",
        "HumanFragment.kt",
        "HumanFragmentSelections.kt",
    )
  }

  companion object {
    private fun List<File>.graphqlFiles(): Set<File> {
      return flatMap {
        it.walk().filter { it.extension == "graphql" }.toList()
      }.toSet()
    }

    private fun List<File>.kotlinFiles(): Set<File> {
      return flatMap {
        it.walk().filter { it.extension == "kt" }.toList()
      }.toSet()
    }

    private fun File.assertContents(vararg files: String) {
      val expected = files.toSet()
      val actual = walk().filter { it.isFile }.map { it.name }.toSet()

      assert(expected == actual) {
        "expected:\n${expected.prettify()}\nactual:\n${actual.prettify()}"
      }
    }

    // A method that makes it easy to copy/paste the results when updating the codegen
    private fun Set<String>.prettify() = sortedBy { it }.map { "\"$it\"" }.joinToString(",\n")
  }
}