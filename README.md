mtkp/ring-dev
=============

Run a development ring server with a `deps.edn` project.

This tool is designed to fill a gap when replacing leiningen with tools.deps.alpha
if previously relying on the useful
[lein-ring](https://github.com/weavejester/lein-ring) plugin.
ring-dev enables basic functionality for running a development ring jetty
server, similar to `lein ring server`.
Currently there are no plans to implement packaging commands that are found in
lein-ring (e.g. `lein ring uberwar`).

# Usage

Add an alias to `deps.edn` (using `server` as an example, if you choose a different
alias name, then the subsequent example commands should be changed accordingly)

```clj
{...
 :aliases
 {:server {:extra-deps {mtkp/ring-dev {:git/url "https://github.com/mtkp/ring-dev"
                                       :sha "...."}}
           :main-opts ["-m" "mtkp.ring-dev.main" "your.ns/your-handler"]}}}
```

Then start a server using the alias added to `deps.edn`

```sh
clj -Aserver
```

## Options

```sh
clj -Aserver --port 45678    # start the server at port 45678
clj -Aserver --browser       # open the server root in the default system browser
clj -Aserver --no-reload     # disable runtime namespace reloading
clj -Aserver --ring-debug --ring-spec # troubleshoot bad handler behavior
```

All options can be added to either `:main-opts` or included at the command line.
Run with `--help` to see the full list of options and default values (`--help` will not
start the server)

```sh
clj -Aserver --help
```

See `example/` for a working example.
