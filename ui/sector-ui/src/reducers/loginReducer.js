export default (state = {}, action) => {
    switch (action.type) {
        case 'SET_JWT':
            return {
                ...state,
                jwt: action.jwt,
                jwtLoaded: true
            };
        case 'UN_SET_JWT':
            return {
                ...state,
                jwt: null,
                jwtLoaded: false
            };
        default:
            return state
    }
}
