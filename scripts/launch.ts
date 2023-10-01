const JAVA_HOME = Deno.args.includes("--java-home")
    ? Deno.args[Deno.args.indexOf("--java-home") + 1]
    : "java";

const MAIN_CLASS = "com.mojang.ld22.Game";
const GAME_JAR = Deno.args.includes("--jar")
    ? Deno.args[Deno.args.indexOf("--jar") + 1]
    : "../build/libs/minicraft-minus-0.1.0.jar";

const dependencies = [
    {
        dependency: "com.fasterxml.jackson.core:jackson-core:2.15.2",
    },
    {
        dependency: "com.fasterxml.jackson.core:jackson-databind:2.15.2",
    },
    {
        dependency: "com.fasterxml.jackson.core:jackson-annotations:2.15.2",
    },
    {
        dependency: "com.google.guava:guava:32.1.2-jre",
    },
    {
        dependency: "commons-io:commons-io:2.14.0",
    },
    {
        dependency: "org.apache.commons:commons-lang3:3.13.0",
    },
    {
        dependency: "it.unimi.dsi:fastutil:8.5.12",
    },
    {
        dependency: "org.joml:joml:1.10.5",
    },
];

function generateFullDependecyInfo(
    dependency: string | { dependency: string }
): {
    group: string;
    name: string;
    version: string;
    url: string;
    path: string;
} {
    const name =
        typeof dependency == "string" ? dependency : dependency.dependency;
    const libgroup = name.substring(0, name.indexOf(":")).replaceAll(".", "/");
    const libname = name.substring(
        name.indexOf(":") + 1,
        name.lastIndexOf(":")
    );
    const libversion = name.substring(name.lastIndexOf(":") + 1);
    const path =
        libgroup +
        "/" +
        libname +
        "/" +
        libversion +
        "/" +
        libname +
        "-" +
        libversion +
        ".jar";

    const url = "https://repo.maven.apache.org/maven2/" + path;
    return {
        group: libgroup,
        name: libname,
        version: libversion,
        url,
        path,
    };
}

function exists(path: string) {
    try {
        return Deno.statSync(path) != null;
    } catch (e) {
        return false;
    }
}

const cp: string[] = [];

if (dependencies.length > 0) {
    Deno.mkdirSync("libraries", { recursive: true });

    for (const lib of dependencies) {
        const { path, url } = generateFullDependecyInfo(lib);

        if (!exists("libraries/" + path)) {
            const data = new Uint8Array(
                await(
                    await fetch(url, {
                        headers: {
                            "Content-Type": "application/octet-stream",
                        },
                    })
                ).arrayBuffer()
            );
            Deno.mkdirSync(
                "libraries/" + path.substring(0, path.lastIndexOf("/")),
                {
                    recursive: true,
                }
            );
            Deno.writeFileSync("libraries/" + path, data);
        }

        cp.push(Deno.cwd().replaceAll("\\", "/") + "/libraries/" + path);
    }
}

const cmd = new Deno.Command(JAVA_HOME, {
    args: ["-cp", [...cp, GAME_JAR].join(";"), MAIN_CLASS],
    stdout: "piped",
    cwd: "../run",
});
const child = cmd.spawn();
child.stdout.pipeTo(Deno.stdout.writable);
await child.status;
