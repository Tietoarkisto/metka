SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: api_user; Type: TABLE; Schema: public; Owner: metka
--

CREATE TABLE public.api_user (
    api_user_id bigint NOT NULL,
    created_by character varying(255),
    last_access timestamp without time zone,
    name character varying(255),
    role character varying(255),
    secret character varying(255)
);


ALTER TABLE public.api_user OWNER TO metka;

--
-- Name: configuration; Type: TABLE; Schema: public; Owner: metka
--

CREATE TABLE public.configuration (
    configuration_id bigint NOT NULL,
    data text,
    type character varying(255),
    version integer
);


ALTER TABLE public.configuration OWNER TO metka;

--
-- Name: gui_configuration; Type: TABLE; Schema: public; Owner: metka
--

CREATE TABLE public.gui_configuration (
    gui_configuration_id bigint NOT NULL,
    data text,
    type character varying(255),
    version integer
);


ALTER TABLE public.gui_configuration OWNER TO metka;

--
-- Name: indexer_command_queue; Type: TABLE; Schema: public; Owner: metka
--

CREATE TABLE public.indexer_command_queue (
    indexer_command_id bigint NOT NULL,
    action character varying(255),
    created timestamp without time zone,
    handled timestamp without time zone,
    parameters character varying(255),
    path character varying(255),
    repeated boolean,
    requested timestamp without time zone,
    type character varying(255)
);


ALTER TABLE public.indexer_command_queue OWNER TO metka;

--
-- Name: misc_json; Type: TABLE; Schema: public; Owner: metka
--

CREATE TABLE public.misc_json (
    key character varying(255) NOT NULL,
    data text
);


ALTER TABLE public.misc_json OWNER TO metka;

--
-- Name: revision; Type: TABLE; Schema: public; Owner: metka
--

CREATE TABLE public.revision (
    revision_no integer NOT NULL,
    revisionable_id bigint NOT NULL,
    data text,
    index_status character varying(255),
    indexing_handled timestamp without time zone,
    indexing_requested timestamp without time zone,
    latest character varying(255),
    state character varying(255)
);


ALTER TABLE public.revision OWNER TO metka;

--
-- Name: revisionable; Type: TABLE; Schema: public; Owner: metka
--

CREATE TABLE public.revisionable (
    type character varying(30) NOT NULL,
    revisionable_id bigint NOT NULL,
    cur_approved_no integer,
    latest_revision_no integer,
    removal_date timestamp without time zone,
    removed boolean,
    removed_by character varying(255),
    study_variable_study bigint,
    study_variables_id bigint,
    varid character varying(255),
    study_variables_study bigint,
    study_attachment_study bigint,
    study_id character varying(255),
    publication_id bigint
);


ALTER TABLE public.revisionable OWNER TO metka;

--
-- Name: saved_expert_search; Type: TABLE; Schema: public; Owner: metka
--

CREATE TABLE public.saved_expert_search (
    saved_expert_search_id bigint NOT NULL,
    description character varying(255),
    query character varying(255),
    saved_at date,
    saved_by character varying(255),
    title character varying(255)
);


ALTER TABLE public.saved_expert_search OWNER TO metka;

--
-- Name: sequence_holder; Type: TABLE; Schema: public; Owner: metka
--

CREATE TABLE public.sequence_holder (
    sequence_id character varying(255) NOT NULL,
    sequence bigint
);


ALTER TABLE public.sequence_holder OWNER TO metka;
