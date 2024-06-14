const headers = ['productId', 'title', 'author', 'publishDate', 'category', 'description', "image"];

function handleUserRole() {
    const url = window.location.href;
    if (url.toLowerCase().includes("customer")) role = "customer";
    else role = "admin"
    if (role === "customer") {
        const addLink = document.getElementById("add-products")
        addLink ? addLink.style.display = "none" : null;
    }
}

async function deleteProduct(productId) {
    try {
        const response = await fetch(`/product/${productId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            window.alert("Deleted Successfully");
            await fetchProducts();
        } else {
            const errorData = await response.json();
            window.alert('Delete Product failed: ' + errorData.message);
        }
    } catch (error) {
        console.error('Error deleting product:', error);
        window.alert('An error occurred. Please try again.');
    }
}

async function populateData(headers, data) {
    const table = document.getElementById("table");
    // clear table
    table.innerHTML = "";
    if (data.length === 0) {
        const noData = document.createElement("p");
        noData.innerHTML = "No Data Found";
        table.append(noData);
    }

    const thead = document.createElement("thead");
    const tr = document.createElement("tr")
    for (let i = 0; i <= headers.length - 1; i++) {
        const td = document.createElement("th")
        td.innerHTML = headers[i];
        tr.append(td);
    }
    thead.append(tr);
    table.append(thead);
    const tbody = document.createElement("tbody");
    for (let j = 0; j <= data.length - 1; j++) {
        const tr2 = document.createElement("tr")
        for (let i = 0; i <= headers.length - 1; i++) {
            const td = document.createElement("td")
            if (headers[i] === "image") {
                const image = document.createElement("img")
                image.src = data[j][headers[i]];
                td.append(image);
            } else {
                td.innerHTML = data[j][headers[i]];
            }
            tr2.append(td);
        }

        // append view
        const td2 = document.createElement("td")
        const view = document.createElement("a")
        view.href = `/Admin/product-view.html?id=${data[j].productId}`
        view.innerHTML = "view";
        view.classList.add("btn")
        view.classList.add("btn-info")
        td2.append(view)
        tr2.append(td2);

        if (role === "admin") {
            // append edit and delete button
            const td3 = document.createElement("td")
            const edit = document.createElement("a")
            edit.href = `/Admin/product-edit.html?id=${data[j].productId}`
            edit.innerHTML = "edit";
            edit.classList.add("btn")
            edit.classList.add("btn-warning")
            td3.append(edit)
            const td4 = document.createElement("td")
            const del = document.createElement("button")
            del.onclick = () => deleteProduct(data[j].productId);
            del.innerHTML = "delete";
            del.classList.add("btn")
            del.classList.add("btn-danger")
            td4.append(del)

            //append action buttons to row
            tr2.append(td3);
            tr2.append(td4);
            tbody.append(tr2)
        } else {
            tbody.append(tr2)
        }
    }
    table.append(tbody);
}

async function fetchProducts() {
    try {
        const response = await fetch('/products', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const responseData = await response.json();
            console.log({responseData})
            products = Object.freeze(responseData);
            await filterProducts();
        } else {
            const errorData = await response.json();
            window.alert('Fetch Product failed: ' + errorData.message);
        }
    } catch (error) {
        console.error('Error during login:', error);
        window.alert('An error occurred. Please try again.');
    }
}

async function filterProducts() {
    const searchInput = document.getElementById("search").value;
    const categorySelect = document.getElementById(("category")).value;

    console.log(searchInput, categorySelect, products);
    if (searchInput === "" && categorySelect === "")
        return await populateData(headers, products);
    const filteredProducts = products.filter(product => {
            const title = product.title;
            const author = product.author;
            return (title.includes(searchInput) || author.includes(searchInput)) && product.category.includes(categorySelect);
        }
    )
    console.log({filteredProducts});
    await populateData(headers, filteredProducts);
}

async function formDeserialize(form, data) {
    for (const [key, val] of (new URLSearchParams(data)).entries()) {
        const input = form.elements[key];
        if (input.type === 'checkbox') {
            input.checked = !!val;
        } else {
            input.value = val;
        }
    }
}

async function populateFormData(data) {
    const form = document.getElementById("edit-form");
    const formData = new FormData();

    formData.append("title", data.title);
    formData.append("author", data.author);
    formData.append("publishDate", data.publishDate);
    formData.append("category", data.category);
    formData.append("description", data.description);
    // formData.append("image", `/images/${data.image}`);

    console.log({formData});
    await formDeserialize(form, formData);
}

async function loadProductDetails() {
    const params = new URLSearchParams(window.location.search);
    productId = params.get('id');
    try {
        const response = await fetch(`/product/${productId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const responseData = await response.json();
            await populateFormData(responseData);
        } else {
            const errorData = await response.json();
            window.alert('Fetch Product failed: ' + errorData.message);
        }
    } catch (error) {
        console.error('Error during login:', error);
        window.alert('An error occurred. Please try again.');
    }
}

async function submitFormEdit(e) {
    e.preventDefault();


    const form = e.target;
    const formData = new FormData();
    const file = form.image.files[0];
    formData.append('image', file);
    formData.append("title", form.title.value);
    formData.append("author", form.author.value);
    formData.append("publishDate", form.publishDate.value);
    formData.append("category", form.category.value);
    formData.append("description", form.description.value);

    try {
        const response = await fetch(`/product/${productId}`, {
            method: 'PATCH',
            body: formData,
        });

        if (response.ok) {
            const responseData = await response.json();
            window.alert(responseData.message);
            window.location.assign('product-index.html');
        } else {
            const errorData = await response.json();
            window.alert('Create Product failed: ' + errorData.message); // Show an alert with the error message
        }
    } catch (error) {
        console.error('Error during product creation:', error);
        window.alert('An error occurred. Please try again.');
    }

}

async function submitFormCreate(e) {
    e.preventDefault();

    const form = e.target;
    const formData = new FormData();
    const file = form.image.files[0];
    formData.append('image', file);
    formData.append("title", form.title.value);
    formData.append("author", form.author.value);
    formData.append("publishDate", form.publishDate.value);
    formData.append("category", form.category.value);
    formData.append("description", form.description.value);

    try {
        const response = await fetch("/product", {
            method: 'POST',
            body: formData,
        });

        if (response.ok) {
            const responseData = await response.json();
            window.alert(responseData.message);
            window.location.assign('product-index.html');
        } else {
            const errorData = await response.json();
            window.alert('Create Product failed: ' + errorData.message); // Show an alert with the error message
        }
    } catch (error) {
        console.error('Error during product creation:', error);
        window.alert('An error occurred. Please try again.');
    }
}