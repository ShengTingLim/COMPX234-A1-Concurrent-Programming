# COMPX234-A1: Concurrent Programming

![Language](https://img.shields.io/badge/Language-Java-blue.svg)

### The Problem

The assignment was to simulate a print queue system. Multiple "machine" threads produce print requests and add them to a queue, while a few "printer" threads consume requests from the queue.

Without any controls, when the queue gets full, new requests would overwrite the oldest ones.
